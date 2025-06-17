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

                        Block highestBlock = world.getHighestBlockAt(x + dx, z + dz);
                        Location particleLocation = highestBlock.getLocation().add(0.5, 1, 0.5);
                        world.spawnParticle(getParticle(), particleLocation, 10, new Particle.DustOptions(Color.RED, 1));
                    }

                    time--;
                }
            }.runTaskTimerAsynchronously(RClaim.getInstance(), 0, 5);
        } else if (viewerMode.equalsIgnoreCase("border")) {
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

    private static Particle getParticle(){
        String currentVersion = Bukkit.getVersion();
        if (currentVersion.equals("1.21") || currentVersion.equals("1.21.1")){
            return Particle.valueOf("DUST");
        } else {
            return Particle.valueOf("REDSTONE");
        }
    }

}
