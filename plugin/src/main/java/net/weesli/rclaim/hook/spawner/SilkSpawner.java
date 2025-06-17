package net.weesli.rclaim.hook.spawner;

import de.corneliusmay.silkspawners.plugin.events.SpawnerPlaceEvent;
import net.weesli.rclaim.RClaim;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SilkSpawner implements IClaimSpawner {

    public SilkSpawner(){
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlaceSpawner(SpawnerPlaceEvent event){
                if (!availableArea(event.getPlayer(), event.getLocation())){
                    event.setCancelled(true);
                }
            }
        }, RClaim.getInstance());
    }

    @Override
    public String getName() {
        return "SilkSpawner";
    }
}
