package net.weesli.rclaim.hook.map;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.ClaimMap;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rclaim.api.model.SubClaim;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.*;

public class HDynmap implements ClaimMap {

    private DynmapCommonAPI api;
    private MarkerAPI markerAPI;
    private MarkerSet set;

    public HDynmap() {
        Plugin dynmap = RClaim.getInstance().getServer()
                .getPluginManager()
                .getPlugin("dynmap");

        if (dynmap instanceof DynmapCommonAPI) {
            this.api = (DynmapCommonAPI) dynmap;
            this.markerAPI = api.getMarkerAPI();

            set = markerAPI.getMarkerSet("rclaim");
            if (set == null) {
                set = markerAPI.createMarkerSet("rclaim", "Claims", null, false);
            }
        }
    }

    @Override
    public void update(Claim claim) {
        if (set == null) return;

        ClaimImpl impl = (ClaimImpl) claim;

        createOrUpdateChunk(
                impl.getID(),
                impl.getCenter().getWorld().getName(),
                impl.getX(),
                impl.getZ(),
                Bukkit.getOfflinePlayer(impl.getOwner()).getName()
        );

        for (SubClaim sub : impl.getSubClaims()) {
            createOrUpdateChunk(
                    impl.getID() + "_sub_" + sub.getX() + "_" + sub.getZ(),
                    impl.getCenter().getWorld().getName(),
                    sub.getX() << 4,
                    sub.getZ() << 4,
                    Bukkit.getOfflinePlayer(impl.getOwner()).getName() + " (Sub)"
            );
        }
    }

    private void createOrUpdateChunk(
            String id,
            String world,
            int blockX,
            int blockZ,
            String label
    ) {
        AreaMarker marker = set.findAreaMarker(id);

        double[] x = {
                blockX,
                blockX + 16,
                blockX + 16,
                blockX
        };

        double[] z = {
                blockZ,
                blockZ,
                blockZ + 16,
                blockZ + 16
        };

        if (marker == null) {
            marker = set.createAreaMarker(
                    id,
                    label,
                    true,
                    world,
                    x,
                    z,
                    false
            );
        } else {
            marker.setCornerLocations(x, z);
            marker.setLabel(label);
        }

        if (marker != null) {
            marker.setFillStyle(0.5, 0x66FF66);
            marker.setLineStyle(1, 1.0, 0xFFFFFF);
        }
    }

    @Override
    public void delete(Claim claim) {
        if (set == null) return;

        ClaimImpl impl = (ClaimImpl) claim;

        AreaMarker main = set.findAreaMarker(impl.getID());
        if (main != null) main.deleteMarker();

        impl.getSubClaims().forEach(sub -> {
            AreaMarker subMarker = set.findAreaMarker(
                    impl.getID() + "_sub_" + sub.getX() + "_" + sub.getZ()
            );
            if (subMarker != null) subMarker.deleteMarker();
        });
    }

    @Override
    public void disable() {
        if (set != null) {
            set.deleteMarkerSet();
        }
    }
}
