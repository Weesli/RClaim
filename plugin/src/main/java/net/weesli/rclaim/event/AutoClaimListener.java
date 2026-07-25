package net.weesli.rclaim.event;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.config.lang.LangConfig;
import net.weesli.rclaim.hook.other.HWorldGuard;
import net.weesli.rclaim.manager.AutoClaimManager;
import net.weesli.rclaim.util.BaseUtil;
import net.weesli.rclaim.util.PermissionUtil;
import org.bukkit.Chunk;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class AutoClaimListener implements Listener {

    public AutoClaimListener() {
        RClaim.getInstance().getServer().getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        if (e.getFrom().getChunk().equals(e.getTo().getChunk())) return;

        Player player = e.getPlayer();
        if (!AutoClaimManager.isEnabled(player.getUniqueId())) return;

        if (!HWorldGuard.isAreaEnabled(player)) return;
        if (!BaseUtil.isActiveWorld(player.getWorld().getName())) return;

        Chunk chunk = e.getTo().getChunk();
        // already claimed (by anyone) or too close to another claim: skip silently, this is expected while walking around
        if (RClaim.getInstance().getClaimManager().isSuitable(chunk)) return;
        if (!BaseUtil.isBetweenAnyClaim(chunk)) return;

        if (!PermissionUtil.checkPlayerClaimLimit(player)) {
            AutoClaimManager.disable(player.getUniqueId());
            LangConfig.sendActionBarToPlayer("AUTO_CLAIM_LIMIT_REACHED", player);
            return;
        }

        int claimCostPerDay = ConfigLoader.getConfig().getClaimSettings().getClaimCostPerDay();
        int totalClaimCost = claimCostPerDay * ConfigLoader.getConfig().getClaimSettings().getClaimDuration();

        if (RClaim.getInstance().getEconomyManager().getEconomyIntegration().isActive()) {
            if (!RClaim.getInstance().getEconomyManager().getEconomyIntegration().hasEnough(player, totalClaimCost)) {
                AutoClaimManager.disable(player.getUniqueId());
                LangConfig.sendActionBarToPlayer("AUTO_CLAIM_NOT_ENOUGH_MONEY", player);
                return;
            }
            RClaim.getInstance().getEconomyManager().getEconomyIntegration().withdraw(player, totalClaimCost);
        }

        RClaim.getInstance().getClaimManager().createClaim(chunk, player);
        LangConfig.sendActionBarToPlayer("AUTO_CLAIM_CHUNK_CLAIMED", player);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        AutoClaimManager.disable(e.getPlayer().getUniqueId());
    }
}
