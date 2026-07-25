package net.weesli.rclaim.ui.inventories;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.lang.MenuConfig;
import net.weesli.rclaim.marketplace.ListingType;
import net.weesli.rclaim.marketplace.MarketListing;
import net.weesli.rclaim.marketplace.MarketplaceManager;
import net.weesli.rclaim.ui.ClaimInventory;
import net.weesli.rozslib.inventory.types.PageableInventory;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.stream.Collectors;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class ClaimMarketMenu extends ClaimInventory {

    private static final MenuConfig.PageableMenu menu = ConfigLoader.getMenuConfig().getMarketMenu();

    @Override
    public void openInventory(Player player, Claim ignoredClaim) {
        PageableInventory inventory = new PageableInventory(PlaceholderAPI.setPlaceholders(player, menu.getTitle()), menu.getSize(),
                menu.getPreviousItem().asClickableItemStack(player),
                menu.getNextItem().asClickableItemStack(player));
        inventory.setLayout(menu.getFillerSlots()
                .stream()
                .mapToInt(Integer::intValue)
                .toArray()).fill(new ItemStack(Material.GRAY_STAINED_GLASS_PANE), menu.isAutoFill());

        for (MarketListing listing : MarketplaceManager.getListings().values()) {
            Claim listedClaim = RClaim.getInstance().getClaimManager().getClaim(listing.claimId());
            if (listedClaim == null) continue;

            String templateKey = listing.type() == ListingType.SALE ? "item-sale" : "item-rent";
            MenuConfig.MenuItem template = menu.getItems().get(templateKey);
            if (template == null) continue;

            String ownerName = Bukkit.getOfflinePlayer(listing.sellerId()).getName();
            String claimName = listedClaim.getDisplayName() != null ? listedClaim.getDisplayName() : listedClaim.getID();
            String durationText = (listing.rentDurationSeconds() / 3600) + "h";

            ItemStack itemStack = getItemStack(template, player);
            ItemMeta meta = itemStack.getItemMeta();
            meta.setDisplayName(meta.hasDisplayName() ? meta.getDisplayName()
                    .replace("<claim>", claimName) : null);
            meta.setLore(meta.hasLore() ? meta.getLore().stream().map(line -> line
                    .replace("<owner>", ownerName == null ? "?" : ownerName)
                    .replace("<x>", String.valueOf(listedClaim.getX()))
                    .replace("<z>", String.valueOf(listedClaim.getZ()))
                    .replace("<world>", listedClaim.getWorldName())
                    .replace("<price>", String.valueOf(listing.price()))
                    .replace("<duration>", durationText)
            ).collect(Collectors.toList()) : null);
            itemStack.setItemMeta(meta);

            inventory.addItem(itemStack, e -> {
                MarketplaceManager.PurchaseResult result = MarketplaceManager.purchase(player, listing.claimId());
                switch (result) {
                    case SUCCESS -> sendMessageToPlayer(
                            listing.type() == ListingType.SALE ? "MARKET_PURCHASE_SUCCESS" : "MARKET_RENT_SUCCESS", player);
                    case OWN_LISTING -> sendMessageToPlayer("MARKET_CANT_BUY_OWN", player);
                    case NOT_ENOUGH_MONEY -> sendMessageToPlayer("HASNT_MONEY", player);
                    case NOT_LISTED -> sendMessageToPlayer("MARKET_NOT_LISTED", player);
                }
                RClaim.getInstance().getFoliaLib().getScheduler().runNextTick((task) -> {
                    player.closeInventory();
                    RClaim.getInstance().getUiManager().openInventory(player, null, ClaimMarketMenu.class);
                });
            });
        }

        inventory.openDefaultInventory(player);
    }
}
