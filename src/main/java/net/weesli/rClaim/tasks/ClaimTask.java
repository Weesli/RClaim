package net.weesli.rClaim.tasks;

import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.management.ExplodeCause;
import org.bukkit.scheduler.BukkitRunnable;

public class ClaimTask extends BukkitRunnable {

    private String claimId;
    private int time;
    public ClaimTask(String claimId, int time) {
        this.claimId = claimId;
        this.time = time;
        this.runTaskTimerAsynchronously(RClaim.getInstance(),0,20);
    }

    @Override
    public void run() {
        if (time==0){
            ClaimManager.ExplodeClaim(claimId, ExplodeCause.TIME_OUT);
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
