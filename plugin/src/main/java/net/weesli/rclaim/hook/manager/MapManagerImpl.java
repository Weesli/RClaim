package net.weesli.rclaim.hook.manager;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimCreateEvent;
import net.weesli.rclaim.api.events.ClaimDeleteEvent;
import net.weesli.rclaim.api.events.ClaimStatusChangeEvent;
import net.weesli.rclaim.api.events.ClaimSubClaimCreateEvent;
import net.weesli.rclaim.api.events.ClaimTrustEvent;
import net.weesli.rclaim.api.events.ClaimUnTrustEvent;
import net.weesli.rclaim.api.hook.ClaimMap;
import net.weesli.rclaim.api.hook.manager.MapManager;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.hook.map.HBlueMap;
import net.weesli.rclaim.hook.map.HDynmap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.event.EventHandler;

public class MapManagerImpl implements MapManager {

    private ClaimMap integration;

    public MapManagerImpl() {
        if (Bukkit.getPluginManager().isPluginEnabled("dynmap")){
            integration = new HDynmap();
        }else if (Bukkit.getPluginManager().isPluginEnabled("BlueMap")) {
            integration = new HBlueMap();
        }
        if(integration == null) return;
        RClaim.getInstance().getFoliaLib().getScheduler().runTimerAsync(() -> {
            RClaim.getInstance().getCacheManager().getClaims().getCache().values().forEach(claim -> {
                integration.update(claim);
            });
        }, 0, 20L * 5);

        Bukkit.getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    @Override
    public ClaimMap getIntegration() {
        return integration;
    }

    @EventHandler
    public void onDelete(ClaimDeleteEvent e) {
        integration.delete(e.getClaim());
    }

    @EventHandler
    public void onCreate(ClaimCreateEvent e) {
        if (e.isCancelled()) return;
        scheduleUpdate(e.getClaim());
    }

    @EventHandler
    public void onSubClaimCreate(ClaimSubClaimCreateEvent e) {
        if (e.isCancelled()) return;
        scheduleUpdate(e.getOwnClaim());
    }

    @EventHandler
    public void onTrust(ClaimTrustEvent e) {
        if (e.isCancelled()) return;
        scheduleUpdate(e.getClaim());
    }

    @EventHandler
    public void onUnTrust(ClaimUnTrustEvent e) {
        if (e.isCancelled()) return;
        scheduleUpdate(e.getClaim());
    }

    @EventHandler
    public void onStatusChange(ClaimStatusChangeEvent e) {
        scheduleUpdate(e.getClaim());
    }

    /**
     * Claim mutations (create/trust/status change) fire their event before the triggering
     * manager method finishes applying the change (default statuses, cache insertion, member
     * list update). Deferring to the claim's region tick lets that state settle first so the
     * map draws the claim's actual post-change data instead of a stale snapshot.
     */
    private void scheduleUpdate(Claim claim) {
        World world = Bukkit.getWorld(claim.getWorldName());
        if (world == null) return;
        RClaim.getInstance().getFoliaLib().getScheduler().runAtLocation(
                new Location(world, claim.getX(), 0, claim.getZ()),
                (task) -> integration.update(claim)
        );
    }
}
