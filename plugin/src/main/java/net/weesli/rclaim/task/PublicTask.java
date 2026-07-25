package net.weesli.rclaim.task;

import com.tcoded.folialib.FoliaLib;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.config.Config;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.hook.discord.DiscordWebhookListener;

public class PublicTask {

    public PublicTask(FoliaLib lib) {
        lib.getScheduler().runTimer(this::run,0,20L);
    }

    public void run() {
        Config.DiscordWebhookConfig webhookConfig = ConfigLoader.getConfig().getDiscordWebhook();
        for (Claim claimImpl : RClaim.getInstance().getCacheManager().getClaims().getCache().values()){
            if (claimImpl.isExpired()){
                RClaim.getInstance().getClaimManager().explodeClaim(claimImpl.getID(), ExplodeCause.TIME_OUT);
            }else {
                if (webhookConfig.isEnabled() && webhookConfig.isClaimExpiringSoon()) {
                    int thresholdSeconds = webhookConfig.getClaimExpiringSoonHours() * 3600;
                    if (claimImpl.getTimestamp() == thresholdSeconds) {
                        DiscordWebhookListener.notifyExpiringSoon(claimImpl, webhookConfig.getClaimExpiringSoonHours());
                    }
                }
                claimImpl.removeTimestamp(1);
            }
        }
    }
}