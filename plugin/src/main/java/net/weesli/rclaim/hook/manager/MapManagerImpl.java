package net.weesli.rclaim.hook.manager;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimDeleteEvent;
import net.weesli.rclaim.api.hook.ClaimMap;
import net.weesli.rclaim.api.hook.manager.MapManager;
import net.weesli.rclaim.hook.map.HBlueMap;
import net.weesli.rclaim.hook.map.HDynmap;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;

public class MapManagerImpl implements MapManager {

    private ClaimMap integration;

    public MapManagerImpl() {
        if (Bukkit.getPluginManager().isPluginEnabled("dynmap")){
            integration = new HDynmap();
        }else if (Bukkit.getPluginManager().isPluginEnabled("BlueMap")) {
            integration = new HBlueMap();
        }
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
}
