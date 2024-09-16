package net.weesli.rClaim.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.api.RClaimAPI;
import net.weesli.rClaim.api.events.ClaimDeleteEvent;
import net.weesli.rClaim.enums.ExplodeCause;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.ClaimManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

@Command("unclaim")
public class unClaimCommand extends BaseCommand {


    @Default
    public void execute(Player player){
        Claim claim = RClaimAPI.getInstance().getClaim(player.getLocation().getChunk());
        if (claim == null){
            player.sendMessage(RClaim.getInstance().getMessage("YOU_DONT_IN_CLAIM"));
            return;
        }
        if (!claim.isOwner(player.getUniqueId())){
            player.sendMessage(RClaim.getInstance().getMessage("NOT_YOUR_CLAIM"));
            return;
        }
        player.sendMessage(RClaim.getInstance().getMessage("CONFIRM_UNCLAIMED"));
    }

    @SubCommand("confirm")
    public void confirm(Player player){
        Claim claim = RClaimAPI.getInstance().getClaim(player.getLocation().getChunk());
        if (claim == null){
            player.sendMessage(RClaim.getInstance().getMessage("YOU_DONT_IN_CLAIM"));
            return;
        }
        if (!claim.isOwner(player.getUniqueId())){
            player.sendMessage(RClaim.getInstance().getMessage("NOT_YOUR_CLAIM"));
            return;
        }
        boolean isCenter = ClaimManager.getPlayerData(player.getUniqueId()).getClaims().get(0).getID().equals(claim.getID());
        ClaimDeleteEvent event = new ClaimDeleteEvent(claim, ExplodeCause.UNCLAIM, isCenter);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            ClaimManager.removeClaim(claim);
            RClaim.getInstance().getStorage().deleteClaim(claim.getID());
        }
        player.sendMessage(RClaim.getInstance().getMessage("UNCLAIMED_CLAIM"));
        if (isCenter){
            List<Claim> claims = ClaimManager.getPlayerData(player.getUniqueId()).getClaims();
            for (Claim c : claims){
                ClaimManager.removeClaim(c);
                RClaim.getInstance().getStorage().deleteClaim(c.getID());
            }
        }
    }
}
