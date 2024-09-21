package net.weesli.rClaim.hooks.map;

import lombok.Getter;
import net.weesli.rClaim.RClaim;
import org.bukkit.Bukkit;

@Getter
public class MapLoader {

    private HDynmap dynmap;

    public MapLoader(){
        if (RClaim.getInstance().getConfig().getBoolean("options.map.dynmap-support")){
            Bukkit.getConsoleSender().sendMessage("[RClaim] Dynmap support is enabled!");
            dynmap = new HDynmap();
        }
    }

}
