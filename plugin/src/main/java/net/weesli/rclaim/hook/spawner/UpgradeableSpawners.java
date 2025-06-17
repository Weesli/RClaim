package net.weesli.rclaim.hook.spawner;

import me.angeschossen.upgradeablespawners.api.events.spawner.SpawnerCreateEvent;
import net.weesli.rclaim.RClaim;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class UpgradeableSpawners implements IClaimSpawner {

    public UpgradeableSpawners(){
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onSpawnerPlace(SpawnerCreateEvent e){
                if (!availableArea(e.getSpawnerPlayer().getPlayer(), e.getSpawner().getCoordinate().toLocation())){
                    e.setCancelled(true);
                    e.getSpawnerPlayer().getPlayer().sendMessage(RClaim.getInstance().getMessage("YOU_CANT_PLACE_SPAWNER"));
                }
            }
        }, RClaim.getInstance());
    }

    @Override
    public String getName() {
        return "UpgradeableSpawners";
    }
}
