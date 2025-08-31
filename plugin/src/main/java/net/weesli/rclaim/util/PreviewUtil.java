package net.weesli.rclaim.util;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.config.ConfigLoader;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Player;

import java.util.*;

public class PreviewUtil {

    private static final int CHUNK_SIZE = 16;
    private static final long DURATION_TICKS = 20L * 3;

    private static final Map<UUID, List<PreviewBlock>> PREVIEWS = new HashMap<>();

    private record PreviewBlock(Location loc, BlockData original) {}

    public static void previewViewer(Player player) {
        String mode = ConfigLoader.getConfig().getViewerMode();
        World world = player.getWorld();
        org.bukkit.Chunk chunk = player.getLocation().getChunk();

        int baseX = chunk.getX() * CHUNK_SIZE;
        int baseZ = chunk.getZ() * CHUNK_SIZE;

        if (mode.toLowerCase(Locale.ROOT).equals("border")) {
            startWorldBorderTask(player, baseX, baseZ);
        } else {
            showChunkPreview(player);
        }
    }

    public static void showChunkPreview(Player player) {
        clearPreview(player);

        World world = player.getWorld();
        org.bukkit.Chunk chunk = player.getLocation().getChunk();
        int baseX = chunk.getX() * CHUNK_SIZE;
        int baseZ = chunk.getZ() * CHUNK_SIZE;

        int[][] corners = {
                {baseX, baseZ},
                {baseX + 15, baseZ},
                {baseX + 15, baseZ + 15},
                {baseX, baseZ + 15}
        };

        List<PreviewBlock> originals = new ArrayList<>();

        for (int i = 0; i < corners.length; i++) {
            int x = corners[i][0];
            int z = corners[i][1];
            int y = world.getHighestBlockYAt(x, z);

            Location corner = new Location(world, x, y, z);
            Block cornerBlock = corner.getBlock();
            originals.add(new PreviewBlock(corner, cornerBlock.getBlockData()));
            player.sendBlockChange(corner, Bukkit.createBlockData(Material.GOLD_BLOCK));

            Location first, second;
            switch (i) {
                case 0 -> {
                    first = corner.clone().add(1, 0, 0);
                    second = corner.clone().add(0, 0, 1);
                }
                case 1 -> {
                    first = corner.clone().add(-1, 0, 0);
                    second = corner.clone().add(0, 0, 1);
                }
                case 2 -> {
                    first = corner.clone().add(-1, 0, 0);
                    second = corner.clone().add(0, 0, -1);
                }
                case 3 -> {
                    first = corner.clone().add(1, 0, 0);
                    second = corner.clone().add(0, 0, -1);
                }
                default -> { continue; }
            }

            originals.add(new PreviewBlock(first, first.getBlock().getBlockData()));
            originals.add(new PreviewBlock(second, second.getBlock().getBlockData()));
            player.sendBlockChange(first, Bukkit.createBlockData(Material.GLOWSTONE));
            player.sendBlockChange(second, Bukkit.createBlockData(Material.GLOWSTONE));
        }

        PREVIEWS.put(player.getUniqueId(), originals);
        Bukkit.getScheduler().runTaskLater(
                RClaim.getInstance(),
                () -> clearPreview(player),
                DURATION_TICKS
        );
    }

    public static void clearPreview(Player player) {
        List<PreviewBlock> list = PREVIEWS.remove(player.getUniqueId());
        if (list == null) return;

        for (PreviewBlock pb : list) {
            player.sendBlockChange(pb.loc(), pb.original());
        }
    }

    private static void startWorldBorderTask(Player player, int x, int z){
        WorldBorder border = Bukkit.createWorldBorder();
        border.setCenter(x+8,z+8);
        border.setSize(16);
        player.setWorldBorder(border);
        border.setSize(16,5);
        Bukkit.getScheduler().runTaskLater(
                RClaim.getInstance(),
                () -> border.setSize(16, 3),
                DURATION_TICKS
        );
    }
}
