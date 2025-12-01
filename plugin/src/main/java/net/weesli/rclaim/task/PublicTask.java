package net.weesli.rclaim.task;

import com.tcoded.folialib.FoliaLib;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.enums.ExplodeCause;

public class PublicTask {

    public PublicTask(FoliaLib lib) {
        lib.getScheduler().runTimer(this::run,0,20L);
    }

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