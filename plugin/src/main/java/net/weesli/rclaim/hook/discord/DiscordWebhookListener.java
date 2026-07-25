package net.weesli.rclaim.hook.discord;

import me.clip.placeholderapi.PlaceholderAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.enums.ExplodeCause;
import net.weesli.rclaim.api.events.ClaimCreateEvent;
import net.weesli.rclaim.api.events.ClaimDeleteEvent;
import net.weesli.rclaim.api.events.ClaimTrustEvent;
import net.weesli.rclaim.api.events.ClaimUnTrustEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.Config;
import net.weesli.rclaim.config.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.UUID;

public class DiscordWebhookListener implements Listener {

    public DiscordWebhookListener() {
        RClaim.getInstance().getServer().getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    @EventHandler
    public void onClaimCreate(ClaimCreateEvent event) {
        Config.DiscordWebhookConfig config = ConfigLoader.getConfig().getDiscordWebhook();
        if (!config.isEnabled() || !config.isClaimPurchased()) return;
        notify("DISCORD_CLAIM_PURCHASED", event.getClaim(), event.getClaim().getOwner(), null);
    }

    @EventHandler
    public void onClaimDelete(ClaimDeleteEvent event) {
        Config.DiscordWebhookConfig config = ConfigLoader.getConfig().getDiscordWebhook();
        if (!config.isEnabled()) return;
        if (event.getCause() == ExplodeCause.TIME_OUT) {
            if (!config.isClaimExpired()) return;
            notify("DISCORD_CLAIM_EXPIRED", event.getClaim(), event.getClaim().getOwner(), null);
        } else {
            if (!config.isClaimDeleted()) return;
            notify("DISCORD_CLAIM_DELETED", event.getClaim(), event.getClaim().getOwner(), null);
        }
    }

    @EventHandler
    public void onTrust(ClaimTrustEvent event) {
        Config.DiscordWebhookConfig config = ConfigLoader.getConfig().getDiscordWebhook();
        if (!config.isEnabled() || !config.isMemberAdded()) return;
        notify("DISCORD_MEMBER_ADDED", event.getClaim(), event.getClaim().getOwner(), event.getTarget());
    }

    @EventHandler
    public void onUnTrust(ClaimUnTrustEvent event) {
        Config.DiscordWebhookConfig config = ConfigLoader.getConfig().getDiscordWebhook();
        if (!config.isEnabled() || !config.isMemberRemoved()) return;
        notify("DISCORD_MEMBER_REMOVED", event.getClaim(), event.getClaim().getOwner(), event.getTarget());
    }

    public static void notifyExpiringSoon(Claim claim, int hours) {
        Config.DiscordWebhookConfig config = ConfigLoader.getConfig().getDiscordWebhook();
        if (!config.isEnabled() || !config.isClaimExpiringSoon()) return;
        String message = buildMessage("DISCORD_CLAIM_EXPIRING_SOON", claim, claim.getOwner(), null)
                .replace("<hours>", String.valueOf(hours));
        DiscordWebhookSender.send(message);
    }

    private static void notify(String path, Claim claim, UUID owner, UUID member) {
        DiscordWebhookSender.send(buildMessage(path, claim, owner, member));
    }

    private static String buildMessage(String path, Claim claim, UUID owner, UUID member) {
        String raw = ConfigLoader.getLangConfig().get(path, String.class);
        OfflinePlayer ownerPlayer = owner != null ? Bukkit.getOfflinePlayer(owner) : null;
        String ownerName = ownerPlayer != null && ownerPlayer.getName() != null ? ownerPlayer.getName() : "?";

        String message = raw
                .replace("<player>", ownerName)
                .replace("<x>", String.valueOf(claim.getX()))
                .replace("<z>", String.valueOf(claim.getZ()))
                .replace("<world>", claim.getWorldName())
                .replace("<claim>", claim.getDisplayName() == null ? claim.getID() : claim.getDisplayName());

        if (member != null) {
            OfflinePlayer memberPlayer = Bukkit.getOfflinePlayer(member);
            message = message.replace("<member>", memberPlayer.getName() == null ? "?" : memberPlayer.getName());
        }

        if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI") && ownerPlayer != null) {
            message = PlaceholderAPI.setPlaceholders(ownerPlayer, message);
        }

        return message;
    }
}
