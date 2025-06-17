package net.weesli.rclaim.task;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.enums.ExplodeCause;
import org.bukkit.scheduler.BukkitRunnable;

public class PublicTask extends BukkitRunnable {

    public PublicTask() {
        this.runTaskTimerAsynchronously(RClaim.getInstance(),0,20);
    }

    @Override
    public void run() {
        for (Claim claimImpl : RClaim.getInstance().getCacheManager().getClaims().getCache().values()){
            if (claimImpl.isExpired()){
                RClaim.getInstance().getClaimManager().explodeClaim(claimImpl.getID(), ExplodeCause.TIME_OUT);
            }else {
                claimImpl.removeTimestamp(1);
            }
        }
    }
}