package net.weesli.rclaim.task;

import com.tcoded.folialib.FoliaLib;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.scheduler.BukkitRunnable;


public class HologramUpdater {

    public HologramUpdater(FoliaLib foliaLib){
        foliaLib.getScheduler().runTimerAsync(this::run,0,20L);
    }

    public void run() {
        for (Claim claim : RClaim.getInstance().getCacheManager().getClaims().getCache().values()){
            if (!claim.isEnableBlock())continue;
            if (RClaim.getInstance().getHologramManager().getHologramIntegration().hasHologram(claim.getID())){
                RClaim.getInstance().getHologramManager().getHologramIntegration().updateHologram(claim.getID());
            }else {
                RClaim.getInstance().getHologramManager().getHologramIntegration().createHologram(claim.getID());
            }
        }
    }
}
