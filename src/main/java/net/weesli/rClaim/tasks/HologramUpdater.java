package net.weesli.rClaim.tasks;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.enums.HologramModule;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.modal.Claim;
import org.bukkit.scheduler.BukkitRunnable;


public class HologramUpdater extends BukkitRunnable {

    public HologramUpdater(){
        runTaskTimerAsynchronously(RClaim.getInstance(), 0, 20);
    }

    @Override
    public void run() {
        for (Claim claim : ClaimManager.getClaims()){
            if (!claim.isCenter()){
                continue;
            }
            if (RClaim.getInstance().getHologram().hasHologram(claim.getID())){
                RClaim.getInstance().getHologram().updateHologram(claim.getID());
            }else {
                RClaim.getInstance().getHologram().createHologram(claim.getID());
            }
        }
    }
}
