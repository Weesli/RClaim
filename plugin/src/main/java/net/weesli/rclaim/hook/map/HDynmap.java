package net.weesli.rclaim.hook.map;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.ClaimMap;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rclaim.model.ClaimImpl;
import net.weesli.rclaim.api.model.SubClaim;
import net.weesli.rclaim.util.BaseUtil;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;
import org.dynmap.DynmapCommonAPI;
import org.dynmap.markers.*;

import java.awt.Color;
import java.util.UUID;

public class HDynmap implements ClaimMap {

    private static final int EXPIRING_SOON_COLOR = 0xFF9800;

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
        String ownerName = Bukkit.getOfflinePlayer(impl.getOwner()).getName();
        int color = colorFor(claim);
        String description = buildDescription(claim, ownerName);

        createOrUpdateChunk(
                impl.getID(),
                impl.getCenter().getWorld().getName(),
                impl.getX(),
                impl.getZ(),
                ownerName,
                description,
                color
        );

        for (SubClaim sub : impl.getSubClaims()) {
            createOrUpdateChunk(
                    impl.getID() + "_sub_" + sub.getX() + "_" + sub.getZ(),
                    impl.getCenter().getWorld().getName(),
                    sub.getX() << 4,
                    sub.getZ() << 4,
                    ownerName + " (Sub)",
                    description,
                    color
            );
        }
    }

    private int colorFor(Claim claim) {
        int expiringSoonHours = ConfigLoader.getConfig().getMapSupport().getExpiringSoonHours();
        if (expiringSoonHours > 0 && claim.getTimestamp() <= expiringSoonHours * 3600) {
            return EXPIRING_SOON_COLOR;
        }
        return colorForOwner(claim.getOwner());
    }

    private int colorForOwner(UUID owner) {
        float hue = (owner.hashCode() & 0x7FFFFFFF) % 360 / 360f;
        return Color.HSBtoRGB(hue, 0.55f, 0.95f) & 0xFFFFFF;
    }

    private String buildDescription(Claim claim, String ownerName) {
        String claimName = claim.getDisplayName() != null ? claim.getDisplayName() : claim.getID();
        boolean pvp = claim.checkStatus("PVP");
        boolean explosion = claim.checkStatus("EXPLOSION");

        return "<div class=\"regioninfo\">"
                + "<b>" + escapeHtml(claimName) + "</b><br/>"
                + "Owner: " + escapeHtml(ownerName == null ? "?" : ownerName) + "<br/>"
                + "Members: " + claim.getMembers().size() + "<br/>"
                + "Remaining: " + escapeHtml(BaseUtil.getTimeFormat(claim.getID())) + "<br/>"
                + "PvP: " + (pvp ? "On" : "Off") + " | Explosion: " + (explosion ? "On" : "Off")
                + "</div>";
    }

    private static String escapeHtml(String input) {
        if (input == null) return "";
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }

    private void createOrUpdateChunk(
            String id,
            String world,
            int blockX,
            int blockZ,
            String label,
            String description,
            int color
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
            marker.setFillStyle(0.5, color);
            marker.setLineStyle(1, 1.0, color);
            marker.setDescription(description);
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
