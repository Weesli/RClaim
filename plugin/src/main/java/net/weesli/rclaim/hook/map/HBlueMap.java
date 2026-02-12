package net.weesli.rclaim.hook.map;

import de.bluecolored.bluemap.api.BlueMapAPI;
import de.bluecolored.bluemap.api.markers.MarkerSet;
import de.bluecolored.bluemap.api.markers.ShapeMarker;
import de.bluecolored.bluemap.api.math.Color;
import de.bluecolored.bluemap.api.math.Shape;
import net.weesli.rclaim.api.hook.ClaimMap;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.Bukkit;

public class HBlueMap implements ClaimMap {

    private static final String MARKER_SET_ID = "rclaim";
    private boolean loaded = false;

    public HBlueMap() {
        BlueMapAPI.onEnable(api -> loaded = true);
    }

    @Override
    public void update(Claim claim) {
        if (!loaded) return;

        BlueMapAPI.getInstance()
                .flatMap(api -> api.getWorld(claim.getCenter().getWorld().getUID()))
                .ifPresent(world -> {

                    world.getMaps().forEach(map -> {

                        MarkerSet markerSet = map.getMarkerSets().computeIfAbsent(
                                MARKER_SET_ID,
                                id -> MarkerSet.builder().label("Claims").build()
                        );

                        String ownerName = Bukkit.getOfflinePlayer(claim.getOwner()).getName();

                        drawChunk(
                                markerSet,
                                claim.getID(),
                                claim.getX(),
                                claim.getZ(),
                                claim.getCenter().getY(),
                                ownerName
                        );

                        claim.getSubClaims().forEach(sub -> {
                            drawChunk(
                                    markerSet,
                                    claim.getID() + "_sub_" + sub.getX() + "_" + sub.getZ(),
                                    sub.getX() << 4,
                                    sub.getZ() << 4,
                                    claim.getCenter().getY(),
                                    ownerName + " (Sub)"
                            );
                        });
                    });
                });
    }

    private void drawChunk(
            MarkerSet markerSet,
            String markerId,
            int blockX,
            int blockZ,
            double y,
            String label
    ) {
        Shape shape = Shape.createRect(
                blockX,
                blockZ,
                blockX + 16,
                blockZ + 16
        );

        ShapeMarker marker = ShapeMarker.builder()
                .shape(shape, (float) y)
                .label(label != null ? label : "Claim")
                .fillColor(new Color(102, 255, 102, 80))
                .lineColor(new Color(255, 255, 255, 255))
                .depthTestEnabled(false) // 🔥 çok kritik
                .build();

        markerSet.put(markerId, marker);
    }


    @Override
    public void delete(Claim claim) {
        if (!loaded) return;

        BlueMapAPI.getInstance()
                .flatMap(api -> api.getWorld(claim.getCenter().getWorld().getUID()))
                .ifPresent(world ->
                        world.getMaps().forEach(map -> {
                            MarkerSet set = map.getMarkerSets().get(MARKER_SET_ID);
                            if (set != null) {
                                set.remove(claim.getID());
                            }
                        })
                );
    }

    @Override
    public void disable() {
        loaded = false;
    }
}
