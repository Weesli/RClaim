package net.weesli.rclaim.hook.map;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.events.ClaimDeleteEvent;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.model.ClaimImpl;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.*;


public class HDynmap implements Listener {

    DynmapCommonAPI api;
    MarkerAPI markerAPI;
    MarkerSet set;

    public HDynmap(){
        Plugin dynmap = RClaim.getInstance().getServer().getPluginManager().getPlugin("dynmap");
        if (!(dynmap instanceof DynmapCommonAPI)) {
            RClaim.getInstance().getLogger().severe("dynmap not found or not compatible, disabling HDynmap");
            RClaim.getInstance().getServer().getPluginManager().disablePlugin(RClaim.getInstance());
            return;
        }
        api = (DynmapCommonAPI) dynmap;
        markerAPI = api.getMarkerAPI();
        createMarkset();
        /*Bukkit.getScheduler().runTaskTimerAsynchronously(RClaim.getInstance(), () -> {
            RClaim.getInstance().getCacheManager().getClaims().getCache().values().forEach(this::UpdateMarkers);
        },0,20L);*/
        RClaim.getInstance().getFoliaLib().getScheduler().runTimerAsync(() -> {
            RClaim.getInstance().getCacheManager().getClaims().getCache().values().forEach(this::UpdateMarkers);
        },0,20L);
        Bukkit.getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    private void createMarkset() {
        MarkerSet set = markerAPI.getMarkerSet("rclaim");
        if (set == null) {
            set = markerAPI.createMarkerSet(
                    "rclaim",
                    "Claim",
                    null,
                    false
            );
        }
        this.set = set;
    }



    public void addMarker(ClaimImpl claim) {
        double[] x = new double[]{claim.getX()};
        double[] z = new double[]{claim.getZ()};
        AreaMarker areaMarker = set.createAreaMarker(
                claim.getID(),
                Bukkit.getOfflinePlayer(claim.getOwner()).getName(),
                true,
                claim.getCenter().getWorld().getName(),
                x,
                z,
                true);
        areaMarker.setFillStyle(50, 0x66FF66);
        areaMarker.setCornerLocation(0, claim.getCenter().getX() + 8, claim.getCenter().getZ() + 8);
        areaMarker.setCornerLocation(1, claim.getCenter().getX() - 8, claim.getCenter().getZ() - 8);
        areaMarker.setLineStyle(0,0, 0xFFFFFF);
    }

    private void UpdateMarkers(Claim claim){
        AreaMarker marker = set.findAreaMarker(claim.getID());
        if (marker == null){
            addMarker((ClaimImpl) claim);
        }
    }

    @EventHandler
    public void onDelete(ClaimDeleteEvent e){
        AreaMarker marker = set.findAreaMarker(e.getClaim().getID());
        if (marker!= null){
            marker.deleteMarker();
        }
    }
}
