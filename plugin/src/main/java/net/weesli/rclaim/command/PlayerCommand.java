package net.weesli.rclaim.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.hook.other.HWorldGuard;
import net.weesli.rclaim.ui.inventories.ClaimMainMenu;
import net.weesli.rclaim.ui.inventories.ClaimsMenu;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rclaim.util.PlayerUtil;

import net.weesli.rclaim.util.PreviewUtil;
import org.bukkit.*;
import org.bukkit.entity.Player;

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
        if (RClaim.getInstance().getEconomyManager().getEconomyIntegration().isActive()){
            if (!RClaim.getInstance().getEconomyManager().getEconomyIntegration().hasEnough(player, totalClaimCost)){
                sendMessageToPlayer("HASNT_MONEY", player);
                return;
            }
            RClaim.getInstance().getEconomyManager().getEconomyIntegration().withdraw(player, totalClaimCost);
        }
        if (RClaim.getInstance().getClaimManager().isSuitable(player.getLocation().getChunk())){
            sendMessageToPlayer("IS_NOT_SUITABLE", player);
            return;
        }
        RClaim.getInstance().getClaimManager().createClaim(player.getLocation().getChunk(), player);
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
            claim = RClaim.getInstance().getCacheManager().getClaims().getCache().values().stream().filter(c ->
                    c.getDisplayName().equals(id)).findFirst().orElse(null);
        }
        if (claim == null || !claim.isOwner(player.getUniqueId()) && !claim.isMember(player.getUniqueId())){
            sendMessageToPlayer("NOT_YOUR_CLAIM", player);
            return;
        }
        player.teleportAsync(claim.getBlockLocation().clone().add(0,2,0));
    }
}
