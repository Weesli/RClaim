package net.weesli.rclaim.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import dev.triumphteam.cmd.core.annotation.SubCommand;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimDeleteEvent;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

@Command("unclaim")
public class UnClaimCommand extends BaseCommand {


    @Default
    public void execute(Player player){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null){
            sendMessageToPlayer("NOT_IN_CLAIM", player);
            return;
        }
        if (!claim.isOwner(player.getUniqueId())){
            sendMessageToPlayer("NOT_YOUR_CLAIM", player);
            return;
        }
        sendMessageToPlayer("CONFIRM_UNCLAIMED", player);
    }

    @SubCommand("confirm")
    public void confirm(Player player){
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(player.getLocation().getChunk());
        if (claim == null){
            sendMessageToPlayer("NOT_IN_CLAIM", player);
            return;
        }
        if (!claim.isOwner(player.getUniqueId())){
            sendMessageToPlayer("NOT_YOUR_CLAIM", player);
            return;
        }
        ClaimDeleteEvent event = new ClaimDeleteEvent(claim, ExplodeCause.UNCLAIM);
        Bukkit.getPluginManager().callEvent(event);
        if (!event.isCancelled()){
            RClaim.getInstance().getClaimManager().removeClaim(claim);
            RClaim.getInstance().getStorage().deleteClaim(claim.getID());
        }
        sendMessageToPlayer("UNCLAIMED_CLAIM", player);
    }
}
