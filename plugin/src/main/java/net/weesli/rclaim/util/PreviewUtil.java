package net.weesli.rclaim.util;

import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.config.ConfigLoader;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class PreviewUtil {

    public static void viewClaimRadius(Player player) {
        previewViewer(player);
    }

    private static void previewViewer(Player player){
        String viewerMode = ConfigLoader.getConfig().getViewerMode();
        int x = player.getLocation().getChunk().getX() * 16;
        int z = player.getLocation().getChunk().getZ() * 16;
        int size = 16;
        World world = player.getWorld();
        if (viewerMode.equalsIgnoreCase("particle")){
            startTaskWithParticles(player, player.getLocation());
        } else if (viewerMode.equalsIgnoreCase("border")) {
            startWorldBorderTask(player, x, z, size, world);
        }
    }

    private static Particle getParticle(){
        String currentVersion = Bukkit.getVersion();
        if (currentVersion.equals("1.21") || currentVersion.equals("1.21.1")){
            return Particle.valueOf("DUST");
        } else {
            return Particle.valueOf("REDSTONE");
        }
    }

    private static void spawnParticle(World world, Location loc) {
        world.spawnParticle(getParticle(), loc, 10, new Particle.DustOptions(Color.RED, 1));
    }


    private static void startTaskWithParticles(Player player, Location location) {
        int x = location.getBlockX();
        int z = location.getBlockZ();
        int size = 16;
        World world = location.getWorld();
        new BukkitRunnable() {
            int time = 20;
            @Override
            public void run() {
                if (time == 0) {
                    this.cancel();
                    return;
                }
                for (int i = 0; i < size * 4 - 4; i++) {
                    int dx = i < size ? i : i < size * 2 - 1 ? size - 1 : i < size * 3 - 2 ? size * 3 - 3 - i : 0;
                    int dz = i < size ? 0 : i < size * 2 - 1 ? i - size + 1 : i < size * 3 - 2 ? size - 1 : size * 4 - 4 - i;

                    int finalX = x + dx;
                    int finalZ = z + dz;
                    Block block = world.getBlockAt(finalX, 0, finalZ);
                    if (block != null) {
                        spawnParticle(world, block.getLocation());
                    }
                }
                time--;
            }
        }.runTaskTimer(RClaim.getInstance(), 0, 5);
    }

    private static void startWorldBorderTask(Player player, int x, int z, int size, World world){
        WorldBorder border = Bukkit.createWorldBorder();
        border.setCenter(x+8,z+8);
        border.setSize(16);
        player.setWorldBorder(border);
        border.setSize(16,5);
        new BukkitRunnable() {
            @Override
            public void run() {
                border.reset();
                this.cancel();
            }
        }.runTaskLater(RClaim.getInstance(),60);
    }
}
