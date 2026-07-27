package net.weesli.rclaim.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.activitylog.ActivityAction;
import net.weesli.rclaim.activitylog.ActivityLogStorage;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.hook.other.HCoreProtect;
import net.weesli.rclaim.hook.other.HWorldGuard;
import net.weesli.rclaim.manager.AutoClaimManager;
import net.weesli.rclaim.manager.PendingRollbackManager;
import net.weesli.rclaim.marketplace.MarketplaceManager;
import net.weesli.rclaim.marketplace.RentalStorage;
import net.weesli.rclaim.ui.inventories.ClaimMainMenu;
import net.weesli.rclaim.ui.inventories.ClaimMarketMenu;
import net.weesli.rclaim.ui.inventories.ClaimsMenu;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rclaim.util.DurationUtil;
import net.weesli.rclaim.util.PlayerUtil;

import net.weesli.rclaim.util.PreviewUtil;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.*;
import org.bukkit.entity.Player;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

@Command(value = "claim",alias = "rclaim")
public class PlayerCommand extends BaseCommand {

    @Default
    public void execute(Player player){
        if (!HWorldGuard.isAreaEnabled(player)){
            sendMessageToPlayer("AREA_DISABLED", player);
            return;
        }

        if(!BaseUtil.isActiveWorld(player.getWorld().getName())){
            sendMessageToPlayer("NOT_IN_CLAIMABLE_WORLD", player);
            return;
        }
        // If the player is in their own request when the 'claim' command is used, the administration menu opens. (since 2.2.0)
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim != null){
            if (claim.getOwner().equals(player.getUniqueId())){
                RClaim.getInstance().getUiManager().openInventory(player, claim, ClaimMainMenu.class);
                return;
            }
        }

        if (RClaim.getInstance().getClaimManager().isSuitable(player.getLocation().getChunk())){
            sendMessageToPlayer("IS_NOT_SUITABLE", player);
            return;
        }

        PreviewUtil.previewViewer(player);
        sendMessageToPlayer("PREVIEW_OPENED", player);
    }

    @SubCommand("confirm")
    public void confirm(Player player){
        if (!HWorldGuard.isAreaEnabled(player)){
            sendMessageToPlayer("AREA_DISABLED", player);
            return;
        }
        if(!BaseUtil.isActiveWorld(player.getWorld().getName())){
            sendMessageToPlayer("NOT_IN_CLAIMABLE_WORLD", player);
            return;
        }
        int claimCostPerDay = ConfigLoader.getConfig().getClaimSettings().getClaimCostPerDay();
        int currentDayCount = 0; // this claim current day count 0 for this claim beacuse created new
        int suitableDayCount = ConfigLoader.getConfig().getClaimSettings().getClaimDuration() - currentDayCount;
        int totalClaimCost = claimCostPerDay * suitableDayCount; // for 30 days claim
        if (RClaim.getInstance().getClaimManager().isSuitable(player.getLocation().getChunk())){
            sendMessageToPlayer("IS_NOT_SUITABLE", player);
            return;
        }
        if (RClaim.getInstance().getEconomyManager().getEconomyIntegration().isActive()){
            if (!RClaim.getInstance().getEconomyManager().getEconomyIntegration().hasEnough(player, totalClaimCost)){
                sendMessageToPlayer("HASNT_MONEY", player);
                return;
            }
            RClaim.getInstance().getEconomyManager().getEconomyIntegration().withdraw(player, totalClaimCost);
        }
        RClaim.getInstance().getClaimManager().createClaim(player.getLocation().getChunk(), player);
    }

    @SubCommand("auto")
    public void toggleAutoClaim(Player player){
        boolean enabled = AutoClaimManager.toggle(player.getUniqueId());
        sendMessageToPlayer(enabled ? "AUTO_CLAIM_ENABLED" : "AUTO_CLAIM_DISABLED", player);
    }

    @SubCommand("trust")
    public void trustPlayer(Player player, @Suggestion("name") String targetPlayer){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            sendMessageToPlayer("NOT_IN_CLAIM", player);
            return;
        }
        if (targetPlayer == null || targetPlayer.isEmpty() ){
            sendMessageToPlayer("ENTER_A_PLAYER_NAME", player);
            return;
        }
        if (player.getName().equalsIgnoreCase(targetPlayer)){
            return;
        }
        OfflinePlayer offlinePlayer = PlayerUtil.getPlayer(targetPlayer);
        if (offlinePlayer == null) {
            sendMessageToPlayer("TARGET_NOT_FOUND", player);
            return;
        }
        claim.trustPlayer(player, offlinePlayer.getUniqueId());
    }

    @SubCommand("untrust")
    public void unTrustPlayer(Player player, @Suggestion("name") String targetPlayer){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            sendMessageToPlayer("NOT_IN_CLAIM", player);
            return;
        }
        if (targetPlayer == null || targetPlayer.isEmpty()){
            sendMessageToPlayer("ENTER_A_PLAYER_NAME", player);
            return;
        }
        if (player.getName().equalsIgnoreCase(targetPlayer)){
            return;
        }
        if(PlayerUtil.getPlayer(targetPlayer) != null){
            OfflinePlayer target = PlayerUtil.getPlayer(targetPlayer);
            if (!claim.getMembers().contains(target.getUniqueId())){
                sendMessageToPlayer("NOT_TRUSTED_PLAYER", player);
                return;
            }
            claim.removeMember(target.getUniqueId());
            claim.getClaimPermissions().remove(target.getUniqueId());
            sendMessageToPlayer("UNTRUST_SUCCESS", player);
        } else {
            sendMessageToPlayer("TARGET_NOT_FOUND", player);
        }
    }
    @SubCommand("log")
    public void viewLog(Player player, String page){
        if (!ConfigLoader.getConfig().getActivityLog().isEnabled()){
            sendMessageToPlayer("ACTIVITY_LOG_DISABLED", player);
            return;
        }
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            sendMessageToPlayer("NOT_IN_CLAIM", player);
            return;
        }
        if (!claim.isOwner(player.getUniqueId()) && !claim.isMember(player.getUniqueId())){
            sendMessageToPlayer("ACTIVITY_LOG_NO_PERMISSION", player);
            return;
        }

        int requestedPage = 1;
        if (page != null && !page.isEmpty()){
            try {
                requestedPage = Integer.parseInt(page);
            } catch (NumberFormatException ignored) {
            }
        }

        String claimName = claim.getDisplayName() != null ? claim.getDisplayName() : claim.getID();
        int pageSize = ConfigLoader.getConfig().getActivityLog().getPageSize();

        ActivityLogStorage.get().fetchPageAsync(claim.getID(), requestedPage, pageSize, player, (entries, totalPages) -> {
            player.sendMessage(ColorBuilder.convertColors(
                    ConfigLoader.getLangConfig().get("ACTIVITY_LOG_HEADER", String.class)
                            .replace("<claim>", claimName)
                            .replace("<page>", String.valueOf(Math.min(Math.max(1, requestedPage), totalPages)))
                            .replace("<pages>", String.valueOf(totalPages))
            ));

            if (entries.isEmpty()){
                sendMessageToPlayer("ACTIVITY_LOG_EMPTY", player);
                return;
            }

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM HH:mm").withZone(ZoneId.systemDefault());
            for (var entry : entries){
                String actionLabel = ConfigLoader.getLangConfig().get(actionLangKey(entry.action()), String.class);
                if (entry.detail() != null && !entry.detail().isEmpty()){
                    actionLabel = actionLabel + " (" + entry.detail() + ")";
                }
                OfflinePlayer actor = Bukkit.getOfflinePlayer(entry.playerId());
                String line = ConfigLoader.getLangConfig().get("ACTIVITY_LOG_ENTRY", String.class)
                        .replace("<time>", formatter.format(Instant.ofEpochMilli(entry.timestamp())))
                        .replace("<player>", actor.getName() == null ? "?" : actor.getName())
                        .replace("<action>", actionLabel);
                player.sendMessage(ColorBuilder.convertColors(line));
            }
        });
    }

    private static String actionLangKey(ActivityAction action){
        return switch (action) {
            case ENTER -> "ACTIVITY_ACTION_ENTER";
            case LEAVE -> "ACTIVITY_ACTION_LEAVE";
            case BLOCK_BREAK -> "ACTIVITY_ACTION_BLOCK_BREAK";
            case BLOCK_PLACE -> "ACTIVITY_ACTION_BLOCK_PLACE";
            case DOOR_INTERACT -> "ACTIVITY_ACTION_DOOR_INTERACT";
            case CONTAINER_INTERACT -> "ACTIVITY_ACTION_CONTAINER_INTERACT";
            case MEMBER_ADDED -> "ACTIVITY_ACTION_MEMBER_ADDED";
            case MEMBER_REMOVED -> "ACTIVITY_ACTION_MEMBER_REMOVED";
        };
    }

    @SubCommand("rollback")
    public void rollback(Player player, String arg){
        // hidden/disabled when CoreProtect isn't installed, per the feature's soft-depend requirement
        if (!HCoreProtect.isAvailable()) return;

        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            sendMessageToPlayer("NOT_IN_CLAIM", player);
            return;
        }
        if (!claim.isOwner(player.getUniqueId()) && !player.hasPermission("rclaim.admin.bypass")){
            sendMessageToPlayer("NOT_YOUR_CLAIM", player);
            return;
        }

        if ("confirm".equalsIgnoreCase(arg)){
            PendingRollbackManager.Pending pendingRollback = PendingRollbackManager.consume(player.getUniqueId(), claim.getID());
            if (pendingRollback == null){
                sendMessageToPlayer("ROLLBACK_NO_PENDING", player);
                return;
            }
            HCoreProtect.rollbackClaim(claim, pendingRollback.seconds());
            sendMessageToPlayer("ROLLBACK_STARTED", player);
            return;
        }

        long seconds = DurationUtil.parseSeconds(arg);
        if (seconds <= 0){
            sendMessageToPlayer("ROLLBACK_INVALID_DURATION", player);
            return;
        }
        PendingRollbackManager.request(player.getUniqueId(), claim.getID(), seconds);
        sendMessageToPlayer("ROLLBACK_CONFIRM", player);
    }

    @SubCommand("sell")
    public void sell(Player player, String priceArg){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            sendMessageToPlayer("NOT_IN_CLAIM", player);
            return;
        }
        if (!claim.isOwner(player.getUniqueId())){
            sendMessageToPlayer("NOT_YOUR_CLAIM", player);
            return;
        }
        if (RentalStorage.get().getActiveRental(claim.getID()) != null){
            sendMessageToPlayer("MARKET_CLAIM_CURRENTLY_RENTED", player);
            return;
        }
        if (MarketplaceManager.isListed(claim.getID())){
            sendMessageToPlayer("MARKET_ALREADY_LISTED", player);
            return;
        }
        double price = parsePrice(priceArg);
        if (price <= 0){
            sendMessageToPlayer("MARKET_INVALID_PRICE", player);
            return;
        }
        MarketplaceManager.listForSale(claim.getID(), player.getUniqueId(), price);
        sendMessageToPlayer("MARKET_LISTED_SALE", player);
    }

    @SubCommand("rent")
    public void rent(Player player, String priceArg, String durationArg){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            sendMessageToPlayer("NOT_IN_CLAIM", player);
            return;
        }
        if (!claim.isOwner(player.getUniqueId())){
            sendMessageToPlayer("NOT_YOUR_CLAIM", player);
            return;
        }
        if (RentalStorage.get().getActiveRental(claim.getID()) != null){
            sendMessageToPlayer("MARKET_CLAIM_CURRENTLY_RENTED", player);
            return;
        }
        if (MarketplaceManager.isListed(claim.getID())){
            sendMessageToPlayer("MARKET_ALREADY_LISTED", player);
            return;
        }
        double price = parsePrice(priceArg);
        if (price <= 0){
            sendMessageToPlayer("MARKET_INVALID_PRICE", player);
            return;
        }
        long durationSeconds = DurationUtil.parseSeconds(durationArg);
        if (durationSeconds <= 0){
            sendMessageToPlayer("MARKET_INVALID_DURATION", player);
            return;
        }
        MarketplaceManager.listForRent(claim.getID(), player.getUniqueId(), price, (int) durationSeconds);
        sendMessageToPlayer("MARKET_LISTED_RENT", player);
    }

    @SubCommand("unlist")
    public void unlist(Player player){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            sendMessageToPlayer("NOT_IN_CLAIM", player);
            return;
        }
        if (!claim.isOwner(player.getUniqueId())){
            sendMessageToPlayer("NOT_YOUR_CLAIM", player);
            return;
        }
        if (!MarketplaceManager.isListed(claim.getID())){
            sendMessageToPlayer("MARKET_NOT_LISTED", player);
            return;
        }
        MarketplaceManager.unlist(claim.getID());
        sendMessageToPlayer("MARKET_UNLISTED", player);
    }

    @SubCommand("market")
    public void market(Player player){
        RClaim.getInstance().getUiManager().openInventory(player, null, ClaimMarketMenu.class);
    }

    private static double parsePrice(String priceArg){
        try {
            return Double.parseDouble(priceArg);
        } catch (NumberFormatException | NullPointerException e){
            return -1;
        }
    }

    @SubCommand("list")
    public void list(Player player){
        RClaim.getInstance().getUiManager().openInventory(player,null, ClaimsMenu.class);
    }

    @SubCommand("rename")
    public void rename(Player player, @Join(" ") String name){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            sendMessageToPlayer("NOT_IN_CLAIM", player);
            return;
        }
        if (name == null || name.isEmpty()){
            sendMessageToPlayer("ENTER_A_CLAIM_NAME", player);
            return;
        }
        claim.setDisplayName(name);
        sendMessageToPlayer("RENAME_SUCCESS", player);
    }

    @SubCommand("toggleblock")
    public void toggle(Player player){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            sendMessageToPlayer("NOT_IN_CLAIM", player);
            return;
        }
        claim.toggleBlockStatus();
        if (claim.isEnableBlock()){
            sendMessageToPlayer("BLOCK_ENABLED", player);
        } else {
            sendMessageToPlayer("BLOCK_DISABLED", player);
        }
    }

    @SubCommand("tp")
    public void tp(Player player, @Suggestion("player_claims") String id){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(id);
        if (claim == null){
            claim = RClaim.getInstance().getCacheManager().getClaims().getCache().values().stream().takeWhile(c ->
                    c.getDisplayName().equals(id)).findFirst().orElse(null);
        }
        if (claim == null || !claim.isOwner(player.getUniqueId()) && !claim.isMember(player.getUniqueId())){
            sendMessageToPlayer("NOT_YOUR_CLAIM", player);
            return;
        }
        player.teleportAsync(claim.getBlockLocation().clone().add(0,2,0));
    }
}
