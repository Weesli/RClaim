package net.weesli.rclaim.hook.other;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import net.weesli.rclaim.config.ConfigLoader;
import net.weesli.rozslib.color.ColorBuilder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Map;

public class HWorldGuard {

    public static boolean isAreaEnabled(Player player){
        if (!Bukkit.getPluginManager().isPluginEnabled("WorldGuard")){
            Bukkit.getConsoleSender().sendMessage(ColorBuilder.convertColors("&c[RClaim] No worldGuard found on the server. Please install worldguard or turn off the worldguard setting!"));
            return true;
        }
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();
        if (ConfigLoader.getConfig().getWorldGuard().isEnabled()){
            for (String name : ConfigLoader.getConfig().getWorldGuard().getBannedRegions()){
                RegionManager regions = container.get(BukkitAdapter.adapt(player.getWorld()));
                for (Map.Entry<String, ProtectedRegion> x : regions.getRegions().entrySet()){
                    if (x.getValue().contains(player.getLocation().getBlockX(), player.getLocation().getBlockY(), player.getLocation().getBlockZ())){
                        if (x.getKey().equals(name)){
                            return false;
                        }
                    }
                }
            }
        }
        return true;
    }

}
