package net.weesli.rClaim.tasks;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.enums.ExplodeCause;
import org.bukkit.scheduler.BukkitRunnable;

public class ClaimTask extends BukkitRunnable {

    private String claimId;
    private int time;
    private boolean isCenter;

    public ClaimTask(String claimId, int time, boolean isCenter) {
        this.claimId = claimId;
        this.time = time;
        this.isCenter = isCenter;
        this.runTaskTimer(RClaim.getInstance(),0,20);
    }

    @Override
    public void run() {
        if (time==0){

            ClaimManager.ExplodeClaim(claimId, ExplodeCause.TIME_OUT, isCenter);
            this.cancel();
        }
        time--;
    }

    public String getClaimId() {
        return claimId;
    }

    public int getTime() {
        return time;
    }

    public void addTime(int time) {
        this.time += time;
    }


}
