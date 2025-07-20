package net.weesli.rclaim.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.*;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.hook.other.HWorldGuard;
import net.weesli.rclaim.ui.inventories.ClaimMainMenu;
import net.weesli.rclaim.ui.inventories.ClaimsMenu;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rclaim.util.PlayerUtil;

import net.weesli.rclaim.util.PreviewUtil;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

@Command(value = "claim",alias = "rclaim")
public class PlayerCommand extends BaseCommand {

    @Default
    public void execute(Player player){
        if (!HWorldGuard.isAreaEnabled(player)){
            player.sendMessage(RClaim.getInstance().getMessage("AREA_DISABLED"));
            return;
        }

        if(!BaseUtil.isActiveWorld(player.getWorld().getName())){
            player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIMABLE_WORLD"));
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
            player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
            return;
        }

        PreviewUtil.viewClaimRadius(player);
        player.sendMessage(RClaim.getInstance().getMessage("PREVIEW_OPENED"));
    }

    @SubCommand("confirm")
    public void confirm(Player player){
        if (!HWorldGuard.isAreaEnabled(player)){
            player.sendMessage(RClaim.getInstance().getMessage("AREA_DISABLED"));
            return;
        }
        if(!BaseUtil.isActiveWorld(player.getWorld().getName())){
            player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIMABLE_WORLD"));
            return;
        }
        int claimCostPerDay = ConfigLoader.getConfig().getClaimSettings().getClaimCostPerDay();
        int currentDayCount = 0; // this claim current day count 0 for this claim beacuse created new
        int suitableDayCount = ConfigLoader.getConfig().getClaimSettings().getClaimDuration() - currentDayCount;
        int totalClaimCost = claimCostPerDay * suitableDayCount; // for 30 days claim
        if (RClaim.getInstance().getEconomyManager().getEconomyIntegration().isActive()){
            if (!RClaim.getInstance().getEconomyManager().getEconomyIntegration().hasEnough(player, totalClaimCost)){
                player.sendMessage(RClaim.getInstance().getMessage("HASNT_MONEY"));
                return;
            }
            RClaim.getInstance().getEconomyManager().getEconomyIntegration().withdraw(player, totalClaimCost);
        }
        if (RClaim.getInstance().getClaimManager().isSuitable(player.getLocation().getChunk())){
            player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
            return;
        }
        RClaim.getInstance().getClaimManager().createClaim(player.getLocation().getChunk(), player);
    }

    @SubCommand("trust")
    public void trustPlayer(Player player, @Suggestion("name") String targetPlayer){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIM"));
            return;
        }
        if (targetPlayer == null || targetPlayer.isEmpty() ){
            player.sendMessage(RClaim.getInstance().getMessage("ENTER_A_PLAYER_NAME"));
            return;
        }
        if (player.getName().equalsIgnoreCase(targetPlayer)){
            return;
        }
        OfflinePlayer offlinePlayer = PlayerUtil.getPlayer(targetPlayer);
        if (offlinePlayer == null) {
            player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
            return;
        }
        claim.trustPlayer(player, offlinePlayer.getUniqueId());
    }

    @SubCommand("untrust")
    public void unTrustPlayer(Player player, @Suggestion("name") String targetPlayer){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIM"));
            return;
        }
        if (targetPlayer == null || targetPlayer.isEmpty()){
            player.sendMessage(RClaim.getInstance().getMessage("ENTER_A_PLAYER_NAME"));
            return;
        }
        if (player.getName().equalsIgnoreCase(targetPlayer)){
            return;
        }
        if(PlayerUtil.getPlayer(targetPlayer) != null){
            OfflinePlayer target = PlayerUtil.getPlayer(targetPlayer);
            if (!claim.getMembers().contains(target.getUniqueId())){
                player.sendMessage(RClaim.getInstance().getMessage("NOT_TRUSTED_PLAYER"));
                return;
            }
            claim.removeMember(target.getUniqueId());
            claim.getClaimPermissions().remove(target.getUniqueId());
            player.sendMessage(RClaim.getInstance().getMessage("UNTRUSTED_PLAYER"));
        } else {
            player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
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
            player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIM"));
            return;
        }
        if (name == null || name.isEmpty()){
            player.sendMessage(RClaim.getInstance().getMessage("ENTER_A_CLAIM_NAME"));
            return;
        }
        claim.setDisplayName(name);
        player.sendMessage(RClaim.getInstance().getMessage("RENAME_SUCCESS"));
    }

    @SubCommand("toggleblock")
    public void toggle(Player player){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation());
        if (claim == null){
            player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIM"));
            return;
        }
        claim.toggleBlockStatus();
        if (claim.isEnableBlock()){
            player.sendMessage(RClaim.getInstance().getMessage("BLOCK_ENABLED"));
        } else {
            player.sendMessage(RClaim.getInstance().getMessage("BLOCK_DISABLED"));
        }
    }
}
