package net.weesli.rclaim.hook.other;

import me.SuperRonanCraft.BetterRTP.references.customEvents.RTP_FindLocationEvent;
import net.weesli.rclaim.RClaim;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

public class HBetterRTP implements Listener {

    public HBetterRTP(Plugin plugin){
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onPlayerTeleport(RTP_FindLocationEvent e){
        Location location = e.getLocation();
        if (location==null)return;
        // Cancel teleport if player is in a claim
        RClaim.getInstance().getCacheManager().getClaims().getCache().values().stream()
                .takeWhile(claim -> claim.contains(location))
                .findFirst().ifPresent(claim -> e.setCancelled(true));
    }

}
