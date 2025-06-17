package net.weesli.rclaim.task;

import lombok.Getter;
import lombok.Setter;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rozslib.database.annotation.PrimaryKey;
import org.bukkit.scheduler.BukkitRunnable;


/**
 * This class has been deprecated.
 * It has been replaced by PublicTask, now all claim operations will be organized with one main task instead of a special task for each claim.
 *
 * @since 2.3.0
 */
@Deprecated
@Getter@Setter
public class ClaimTask extends BukkitRunnable {

    @PrimaryKey
    private String claimId;

    private int time;
    public ClaimTask(String claimId, int time) {
        this.claimId = claimId;
        this.time = time;
        this.runTaskTimer(RClaim.getInstance(),0,20);
    }

    @Override
    public void run() {
        if (time==0){
            RClaim.getInstance().getClaimManager().explodeClaim(claimId, ExplodeCause.TIME_OUT);
            this.cancel();
        }
        time--;
    }

    public void addTime(int time) {
        this.time += time;
    }

}
