package net.weesli.rClaim.command;

import dev.triumphteam.cmd.core.BaseCommand;
import dev.triumphteam.cmd.core.annotation.Command;
import dev.triumphteam.cmd.core.annotation.Default;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.modal.Claim;
import net.weesli.rClaim.utils.ClaimManager;
import org.bukkit.entity.Player;

@Command("chome")
public class ClaimHomeCommand extends BaseCommand {

    @Default
    public void execute(Player player) {
        for (Claim claim : ClaimManager.getClaims()){
            if (claim.isMember(player.getUniqueId()) || claim.isOwner(player.getUniqueId())){
                if (claim.getHomeLocation() != null){
                    player.teleport(claim.getHomeLocation());
                    return;
                }
            }
        }
        player.sendMessage(RClaim.getInstance().getMessage("NO_HOME_SET"));
    }
}
