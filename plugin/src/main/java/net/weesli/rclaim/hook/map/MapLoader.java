package net.weesli.rclaim.hook.map;

import lombok.Getter;
import net.weesli.rclaim.config.ConfigLoader;
import org.bukkit.Bukkit;

@Getter
public class MapLoader {

    private HDynmap dynmap;

    public MapLoader(){
        boolean isDynmapEnable = ConfigLoader.getConfig().getMapSupport().isDynmapSupport();
        if (isDynmapEnable){
            Bukkit.getConsoleSender().sendMessage("[RClaim] Dynmap support is enabled!");
            dynmap = new HDynmap();
        }
    }

}
