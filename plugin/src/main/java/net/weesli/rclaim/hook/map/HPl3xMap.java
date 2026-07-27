package net.weesli.rclaim.hook.map;

import net.pl3x.map.core.Pl3xMap;
import net.pl3x.map.core.markers.Point;
import net.pl3x.map.core.markers.layer.SimpleLayer;
import net.pl3x.map.core.markers.marker.Marker;
import net.pl3x.map.core.markers.option.Options;
import net.pl3x.map.core.registry.LayerRegistry;
import net.pl3x.map.core.world.World;
import net.weesli.rclaim.api.hook.ClaimMap;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.SubClaim;
import net.weesli.rclaim.model.ClaimImpl;
import org.bukkit.Bukkit;

import java.awt.Color;

/**
 * Pl3xMap integration.
 *
 * NOTE: this class was written against the author's best recollection of the Pl3xMap
 * "Core" addon API (net.pl3x.map.core.*: Pl3xMap.api(), World/LayerRegistry, SimpleLayer,
 * Marker.rectangle(...), Options). It could not be compiled or verified against a real
 * Pl3xMap jar or its docs in this environment — outbound access to repo.pl3x.net and every
 * other Maven repo was blocked by network policy. Treat this integration as unverified
 * until it has been built and smoke-tested against an actual Pl3xMap install; the class
 * names, method signatures, and the dependency coordinate in build.gradle.kts may need
 * correction.
 */
public class HPl3xMap implements ClaimMap {

    private static final String LAYER_KEY = "rclaim";

    @Override
    public void update(Claim claim) {
        World world = Pl3xMap.api().getWorldRegistry().get(claim.getWorldName());
        if (world == null) return;

        SimpleLayer layer = layerFor(world);
        ClaimImpl impl = (ClaimImpl) claim;
        String ownerName = Bukkit.getOfflinePlayer(impl.getOwner()).getName();

        drawChunk(layer, impl.getID(), impl.getX(), impl.getZ(), ownerName);

        for (SubClaim sub : impl.getSubClaims()) {
            drawChunk(
                    layer,
                    impl.getID() + "_sub_" + sub.getX() + "_" + sub.getZ(),
                    sub.getX() << 4,
                    sub.getZ() << 4,
                    ownerName + " (Sub)"
            );
        }
    }

    private SimpleLayer layerFor(World world) {
        LayerRegistry<?> registry = world.getLayerRegistry();
        SimpleLayer layer = (SimpleLayer) registry.get(LAYER_KEY);
        if (layer == null) {
            layer = new SimpleLayer(LAYER_KEY, () -> "Claims");
            registry.register(LAYER_KEY, layer);
        }
        return layer;
    }

    private void drawChunk(SimpleLayer layer, String id, int blockX, int blockZ, String label) {
        Marker<?> marker = Marker.rectangle(id, Point.of(blockX, blockZ), Point.of(blockX + 16, blockZ + 16));
        marker.setOptions(Options.builder()
                .fillColor(new Color(102, 255, 102, 80).getRGB())
                .strokeColor(new Color(255, 255, 255).getRGB())
                .popup(label != null ? label : "Claim")
                .build());
        layer.addMarker(marker);
    }

    @Override
    public void delete(Claim claim) {
        World world = Pl3xMap.api().getWorldRegistry().get(claim.getWorldName());
        if (world == null) return;

        SimpleLayer layer = (SimpleLayer) world.getLayerRegistry().get(LAYER_KEY);
        if (layer == null) return;

        ClaimImpl impl = (ClaimImpl) claim;
        layer.removeMarker(impl.getID());
        impl.getSubClaims().forEach(sub ->
                layer.removeMarker(impl.getID() + "_sub_" + sub.getX() + "_" + sub.getZ())
        );
    }

    @Override
    public void disable() {
    }
}
