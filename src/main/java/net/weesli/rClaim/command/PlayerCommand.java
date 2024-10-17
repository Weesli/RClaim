package net.weesli.rClaim.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import dev.triumphteam.cmd.core.annotation.Suggestion;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.api.events.TrustedPlayerEvent;
import net.weesli.rClaim.api.events.UnTrustedPlayerEvent;
import net.weesli.rClaim.hooks.HWorldGuard;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.modal.ClaimPlayer;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.utils.PlayerUtils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Optional;

@Command(value = "claim",alias = "rclaim")
public class PlayerCommand extends BaseCommand {

    @Default
    public void execute(Player player){
        if (!HWorldGuard.isAreaEnabled(player)){
            player.sendMessage(RClaim.getInstance().getMessage("AREA_DISABLED"));
            return;
        }
        if(!ClaimManager.checkWorld(player.getWorld().getName())){
            player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIMABLE_WORLD"));
            return;
        }

        if (ClaimManager.isSuitable(player.getLocation().getChunk())){
            player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
            return;
        }
        ClaimManager.viewClaimRadius(player,player.getLocation().getChunk());
        player.sendMessage(RClaim.getInstance().getMessage("PREVIEW_OPENED"));
    }

    @SubCommand("confirm")
    public void confirm(Player player){
        if (!HWorldGuard.isAreaEnabled(player)){
            player.sendMessage(RClaim.getInstance().getMessage("AREA_DISABLED"));
            return;
        }
        if(!ClaimManager.checkWorld(player.getWorld().getName())){
            player.sendMessage(RClaim.getInstance().getMessage("NOT_IN_CLAIMABLE_WORLD"));
            return;
        }
        if (!ClaimManager.getPlayerData(player.getUniqueId()).getClaims().isEmpty()){
            player.sendMessage(RClaim.getInstance().getMessage("CANNOT_CLAIM_MULTIPLE_CLAIMS"));
            return;
        }
        if (RClaim.getInstance().getEconomy().isActive()){
            if (!RClaim.getInstance().getEconomy().hasEnough(player, RClaim.getInstance().getConfig().getInt("claim-settings.claim-cost"))){
                player.sendMessage(RClaim.getInstance().getMessage("HASNT_MONEY"));
                return;
            }
            RClaim.getInstance().getEconomy().withdraw(player, RClaim.getInstance().getConfig().getInt("claim-settings.claim-cost"));
        }
        if (ClaimManager.isSuitable(player.getLocation().getChunk())){
            player.sendMessage(RClaim.getInstance().getMessage("IS_NOT_SUITABLE"));
            return;
        }
        ClaimManager.createClaim(player.getLocation().getChunk(), player, true, "");
        player.sendMessage(RClaim.getInstance().getMessage("SUCCESS_CLAIM_CREATED"));
    }

    @SubCommand("trust")
    public void trust(Player player, @Suggestion("name") String targetPlayer){
        if (targetPlayer == null || targetPlayer.isEmpty()){
            player.sendMessage(RClaim.getInstance().getMessage("ENTER_A_PLAYER_NAME"));
            return;
        }
        if (player.getName().equalsIgnoreCase(targetPlayer)){
            return;
        }
        if(PlayerUtils.getPlayer(targetPlayer) != null){
            OfflinePlayer target = Bukkit.getOfflinePlayer(targetPlayer);
            TrustedPlayerEvent event = new TrustedPlayerEvent(player,target.getPlayer());
            RClaim.getInstance().getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()){
                ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
                List<Claim> claims = player_data.getClaims();
                if (claims.get(0).getMembers().size() >= RClaim.getInstance().getConfig().getInt("options.max-trusted-player")){
                    player.sendMessage(RClaim.getInstance().getMessage("MAX_TRUSTED_PLAYERS"));
                    return;
                }
                if (claims.get(0).getMembers().contains(target.getUniqueId())){
                    player.sendMessage(RClaim.getInstance().getMessage("ALREADY_TRUSTED_PLAYER"));
                    return;
                }
                for (Claim claim : claims){
                    claim.addMember(target.getUniqueId());
                }
                ClaimManager.getPlayerData().put(player.getUniqueId(),player_data);
                player.sendMessage(RClaim.getInstance().getMessage("TRUSTED_PLAYER"));
            }
        } else {
            player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
        }
    }

    @SubCommand("untrust")
    public void untrust(Player player, @Suggestion("name") String targetPlayer){
        if (targetPlayer == null || targetPlayer.isEmpty()){
            player.sendMessage(RClaim.getInstance().getMessage("ENTER_A_PLAYER_NAME"));
            return;
        }
        if (player.getName().equalsIgnoreCase(targetPlayer)){
            return;
        }
        if(PlayerUtils.getPlayer(targetPlayer) != null){
            OfflinePlayer target = RClaim.getInstance().getServer().getOfflinePlayer(targetPlayer);
            ClaimPlayer player_data = ClaimManager.getPlayerData(player.getUniqueId());
            List<Claim> claims = player_data.getClaims();
            if (!claims.get(0).getMembers().contains(target.getUniqueId())){
                player.sendMessage(RClaim.getInstance().getMessage("NOT_TRUSTED_PLAYER"));
                return;
            }
            UnTrustedPlayerEvent event = new UnTrustedPlayerEvent(player,target.getPlayer());
            RClaim.getInstance().getServer().getPluginManager().callEvent(event);
            if (!event.isCancelled()){
                for (Claim claim : claims){
                    claim.removeMember(target.getUniqueId());
                    claim.getClaimPermissions().remove(target.getUniqueId());
                }
                ClaimManager.getPlayerData().put(player.getUniqueId(),player_data);
                player.sendMessage(RClaim.getInstance().getMessage("UNTRUSTED_PLAYER"));
            }
        } else {
            player.sendMessage(RClaim.getInstance().getMessage("TARGET_NOT_FOUND"));
        }
    }

    @SubCommand("sethome")
    public void sethome(Player player){
        Optional<Claim> claim = ClaimManager.getClaims().stream().filter(c -> c.contains(player.getLocation())).findFirst();
        if (!claim.isPresent()){
            player.sendMessage(RClaim.getInstance().getMessage("YOU_DONT_IN_CLAIM"));
        }
        claim.ifPresent(c -> {
            if (c.isOwner(player.getUniqueId())){
                List<Claim> claims = ClaimManager.getPlayerData(player.getUniqueId()).getClaims();
                claims.stream().forEach(c1 -> {
                    c1.setHomeLocation(null);
                });
                c.setHomeLocation(player.getLocation());
                player.sendMessage(RClaim.getInstance().getMessage("HOME_SET"));
            }
        });
    }


    @SubCommand("list")
    public void list(Player player){
        RClaim.getInstance().getUiManager().openInventory(player,null, RClaim.getInstance().getUiManager().getClaimsMenu());
    }

}
