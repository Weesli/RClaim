package net.weesli.rclaim.hook.spawner;

import de.corneliusmay.silkspawners.plugin.events.SpawnerPlaceEvent;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.ClaimSpawner;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class SilkSpawner implements ClaimSpawner {

    public SilkSpawner(){
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlaceSpawner(SpawnerPlaceEvent event){
                if (!availableArea(event.getPlayer(), event.getLocation())){
                    event.setCancelled(true);
                    sendMessageToPlayer("YOU_CANT_PLACE_SPAWNER", event.getPlayer());
                }
            }
        }, RClaim.getInstance());
    }

    @Override
    public String getName() {
        return "SilkSpawner";
    }
}
