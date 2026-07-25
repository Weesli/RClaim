package net.weesli.rclaim.event;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.activitylog.ActivityAction;
import net.weesli.rclaim.activitylog.ActivityLogStorage;
import net.weesli.rclaim.api.events.ClaimEnterEvent;
import net.weesli.rclaim.api.events.ClaimLeaveEvent;
import net.weesli.rclaim.api.events.ClaimTrustEvent;
import net.weesli.rclaim.api.events.ClaimUnTrustEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.InventoryHolder;

/**
 * Logs claim activity purely for the activity log feature. Listens at MONITOR priority with
 * ignoreCancelled = true so it only sees actions that made it past ClaimPermissionListener's
 * permission checks (that listener owns cancelling disallowed actions; this one only records).
 */
public class ClaimActivityListener implements Listener {

    public ClaimActivityListener() {
        RClaim.getInstance().getServer().getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBreak(BlockBreakEvent e) {
        log(e.getPlayer(), e.getBlock().getLocation(), ActivityAction.BLOCK_BREAK, e.getBlock().getType().name());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onPlace(BlockPlaceEvent e) {
        log(e.getPlayer(), e.getBlock().getLocation(), ActivityAction.BLOCK_PLACE, e.getBlock().getType().name());
    }

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onInteract(PlayerInteractEvent e) {
        if (e.getAction() != Action.RIGHT_CLICK_BLOCK) return;
        Block block = e.getClickedBlock();
        if (block == null) return;

        String type = block.getType().name();
        if (block.getState() instanceof InventoryHolder || type.contains("ENDER_CHEST")) {
            log(e.getPlayer(), block.getLocation(), ActivityAction.CONTAINER_INTERACT, type);
        } else if (type.contains("DOOR") || type.contains("GATE")) {
            log(e.getPlayer(), block.getLocation(), ActivityAction.DOOR_INTERACT, type);
        }
    }

    @EventHandler
    public void onEnter(ClaimEnterEvent e) {
        logToClaim(e.getClaim(), e.getPlayer().getUniqueId(), ActivityAction.ENTER, null);
    }

    @EventHandler
    public void onLeave(ClaimLeaveEvent e) {
        logToClaim(e.getClaim(), e.getPlayer().getUniqueId(), ActivityAction.LEAVE, null);
    }

    @EventHandler
    public void onTrust(ClaimTrustEvent e) {
        if (e.isCancelled()) return;
        String targetName = Bukkit.getOfflinePlayer(e.getTarget()).getName();
        logToClaim(e.getClaim(), e.getPlayer(), ActivityAction.MEMBER_ADDED, targetName);
    }

    @EventHandler
    public void onUnTrust(ClaimUnTrustEvent e) {
        if (e.isCancelled()) return;
        String targetName = Bukkit.getOfflinePlayer(e.getTarget()).getName();
        logToClaim(e.getClaim(), e.getPlayer(), ActivityAction.MEMBER_REMOVED, targetName);
    }

    private void log(Player player, Location location, ActivityAction action, String detail) {
        Claim claim = RClaim.getInstance().getClaimManager().getClaim(location);
        if (claim == null) return;
        logToClaim(claim, player.getUniqueId(), action, detail);
    }

    private void logToClaim(Claim claim, java.util.UUID playerId, ActivityAction action, String detail) {
        if (!ConfigLoader.getConfig().getActivityLog().isEnabled()) return;
        ActivityLogStorage.get().logAsync(claim.getID(), playerId, action, detail);
    }
}
