package net.weesli.rClaim.tasks;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.api.RClaimAPI;
import net.weesli.rClaim.api.events.ClaimEnterEvent;
import net.weesli.rClaim.api.events.ClaimLeaveEvent;
import net.weesli.rClaim.utils.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerCheck extends BukkitRunnable {

    private Player player;


    public PlayerCheck(Player player) {
        this.player = player;
        runTaskTimer(RClaim.getInstance(), 0, 20);
    }
    private Chunk previousChunk;

    @Override
    public void run() {
        if (player == null || !player.isOnline()) {
            this.cancel();
            return;
        }

        Chunk currentChunk = player.getChunk();

        if (previousChunk != null && !previousChunk.equals(currentChunk)) {
            Claim previousClaim = RClaimAPI.getInstance().getClaim(previousChunk);
            Claim currentClaim = RClaimAPI.getInstance().getClaim(currentChunk);

            if (previousClaim != null) {
                ClaimLeaveEvent leaveEvent = new ClaimLeaveEvent(previousClaim, player);
                Bukkit.getPluginManager().callEvent(leaveEvent);
            }
            if (currentClaim != null) {
                ClaimEnterEvent enterEvent = new ClaimEnterEvent(currentClaim, player);
                Bukkit.getPluginManager().callEvent(enterEvent);
            }
        }

        previousChunk = currentChunk;
    }
}
