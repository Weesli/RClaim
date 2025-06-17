package net.weesli.rclaim.task;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.scheduler.BukkitRunnable;


public class HologramUpdater extends BukkitRunnable {

    public HologramUpdater(){
        runTaskTimerAsynchronously(RClaim.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        for (Claim claim : RClaim.getInstance().getCacheManager().getClaims().getCache().values()){
            if (RClaim.getInstance().getHologramManager().getHologramIntegration().hasHologram(claim.getID())){
                RClaim.getInstance().getHologramManager().getHologramIntegration().updateHologram(claim.getID());
            }else {
                RClaim.getInstance().getHologramManager().getHologramIntegration().createHologram(claim.getID());
            }
        }
    }
}
