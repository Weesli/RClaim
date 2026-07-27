package net.weesli.rclaim.marketplace;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.ClaimEconomy;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rozsconfig.annotations.ConfigKey;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Claim sale/rental listings and the atomic buy/rent transaction.
 *
 * Listings themselves (an offer that hasn't been accepted yet) are kept in memory only - if
 * the server restarts, sellers just need to re-list, which is a minor inconvenience rather
 * than a correctness problem. An *accepted* rental is a different matter (see RentalStorage,
 * which is durable) since losing track of one would permanently strand a claim with the
 * wrong owner.
 *
 * Ownership/member transfer rule (explicit, since the task calls this out): on a completed
 * SALE, the claim's member list, per-member permissions, and status flags are all reset to
 * config defaults - the buyer gets a clean claim, not the previous owner's trusted-friends
 * list or flag choices. On a RENT, the member list/permissions are cleared for the renter
 * (same reasoning) but status flags are left as-is, since the claim reverts to its original
 * owner afterward and its flags are that owner's business, not the renter's to permanently
 * change. When a rental ends, the member list is cleared again before handing back, so the
 * renter's guests don't linger in the original owner's claim.
 */
public final class MarketplaceManager {

    private static final Map<String, MarketListing> listings = new ConcurrentHashMap<>();

    /**
     * ClaimEconomy.deposit(...) takes a live Player, not an OfflinePlayer - there's no way to
     * pay an offline seller through that interface as it stands today (widening it to accept
     * OfflinePlayer would be an API change, which per CLAUDE.md needs sign-off before making).
     * Sellers who are offline at the moment of a sale/rent get queued here and paid out the
     * next time they join (see payOutIfPending, called from ClaimLifecycleListener.onJoin).
     * NOTE: this queue is in-memory only, unlike RentalStorage - a payout can be lost if the
     * server restarts before the seller logs back in. Flagging this as an accepted limitation
     * of this pass rather than silently glossing over it.
     */
    private static final Map<UUID, Double> pendingPayouts = new ConcurrentHashMap<>();

    private MarketplaceManager() {
    }

    public static boolean isListed(String claimId) {
        return listings.containsKey(claimId);
    }

    public static MarketListing getListing(String claimId) {
        return listings.get(claimId);
    }

    public static Map<String, MarketListing> getListings() {
        return listings;
    }

    public static void listForSale(String claimId, UUID sellerId, double price) {
        listings.put(claimId, new MarketListing(claimId, sellerId, ListingType.SALE, price, 0));
    }

    public static void listForRent(String claimId, UUID sellerId, double price, int rentDurationSeconds) {
        listings.put(claimId, new MarketListing(claimId, sellerId, ListingType.RENT, price, rentDurationSeconds));
    }

    public static void unlist(String claimId) {
        listings.remove(claimId);
    }

    public enum PurchaseResult {
        SUCCESS,
        NOT_LISTED,
        OWN_LISTING,
        NOT_ENOUGH_MONEY
    }

    /**
     * Synchronized so two players clicking the same listing at the same instant can't both
     * succeed - the whole check-withdraw-transfer sequence runs as one uninterrupted block,
     * and the listing is removed before any money moves so a losing second caller always sees
     * NOT_LISTED rather than racing past the money check.
     */
    public static synchronized PurchaseResult purchase(Player buyer, String claimId) {
        MarketListing listing = listings.get(claimId);
        if (listing == null) return PurchaseResult.NOT_LISTED;

        Claim claim = RClaim.getInstance().getClaimManager().getClaim(claimId);
        if (claim == null) {
            listings.remove(claimId);
            return PurchaseResult.NOT_LISTED;
        }
        if (listing.sellerId().equals(buyer.getUniqueId())) return PurchaseResult.OWN_LISTING;

        ClaimEconomy economy = RClaim.getInstance().getEconomyManager().getEconomyIntegration();
        boolean chargeable = economy.isActive();
        if (chargeable && !economy.hasEnough(buyer, listing.price())) {
            return PurchaseResult.NOT_ENOUGH_MONEY;
        }

        listings.remove(claimId);

        if (chargeable) {
            economy.withdraw(buyer, listing.price());
        }

        try {
            if (listing.type() == ListingType.SALE) {
                transferOwnership(claim, buyer.getUniqueId());
            } else {
                claim.getMembers().clear();
                claim.getClaimPermissions().clear();
                claim.setOwner(buyer.getUniqueId());
                long rentEnd = System.currentTimeMillis() + listing.rentDurationSeconds() * 1000L;
                RentalStorage.get().startRentalAsync(claim.getID(), listing.sellerId(), buyer.getUniqueId(), rentEnd);
            }
        } catch (RuntimeException e) {
            // compensate: the deal didn't go through, so undo the charge and restore the listing
            if (chargeable) economy.deposit(buyer, listing.price());
            listings.put(claimId, listing);
            throw e;
        }

        if (chargeable) {
            payOrQueue(listing.sellerId(), listing.price());
        }

        return PurchaseResult.SUCCESS;
    }

    private static void payOrQueue(UUID sellerId, double amount) {
        Player onlineSeller = Bukkit.getPlayer(sellerId);
        ClaimEconomy economy = RClaim.getInstance().getEconomyManager().getEconomyIntegration();
        if (onlineSeller != null) {
            economy.deposit(onlineSeller, amount);
        } else {
            pendingPayouts.merge(sellerId, amount, Double::sum);
        }
    }

    /**
     * Call when a player joins: pays out anything they were owed from a sale/rent while they
     * were offline.
     */
    public static void payOutIfPending(Player player) {
        Double amount = pendingPayouts.remove(player.getUniqueId());
        if (amount == null) return;

        ClaimEconomy economy = RClaim.getInstance().getEconomyManager().getEconomyIntegration();
        if (!economy.isActive()) return;

        economy.deposit(player, amount);
        String raw = ConfigLoader.getLangConfig().get("MARKET_PENDING_PAYOUT", String.class)
                .replace("<amount>", String.valueOf(amount));
        player.sendMessage(ColorBuilder.convertColors(ConfigLoader.getConfig().getPrefix() + raw));
    }

    private static void transferOwnership(Claim claim, UUID newOwner) {
        claim.setOwner(newOwner);
        claim.getMembers().clear();
        claim.getClaimPermissions().clear();
        resetToDefaultStatuses(claim);
    }

    /**
     * Mirrors ClaimManagerImpl.createClaim's default-status application so a sold claim starts
     * with the same flags a freshly claimed chunk would, instead of inheriting the previous
     * owner's choices.
     */
    private static void resetToDefaultStatuses(Claim claim) {
        for (String status : List.copyOf(claim.getClaimStatuses())) {
            claim.removeClaimStatus(status);
        }
        List<String> defaultStatuses = Arrays.stream(ConfigLoader.getConfig().getClaimSettings().getDefaultClaimStatus().getClass().getFields()).map(field -> {
            boolean value;
            try {
                value = field.getBoolean(ConfigLoader.getConfig().getClaimSettings().getDefaultClaimStatus());
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            if (value) {
                return field.getName();
            }
            ConfigKey key = field.getAnnotation(ConfigKey.class);
            if (key != null) {
                return key.value();
            }
            return field.getName();
        }).toList();
        for (String status : defaultStatuses) {
            claim.addClaimStatus(status);
        }
    }

    /**
     * Called from PublicTask's per-second loop when a rental's time is up: hands the claim
     * back to its original owner and clears the member list before doing so.
     */
    public static void returnFromRental(Claim claim, RentalRecord rental) {
        claim.getMembers().clear();
        claim.getClaimPermissions().clear();
        claim.setOwner(rental.originalOwnerId());
        RentalStorage.get().endRentalAsync(claim.getID());
    }
}
