package net.weesli.rclaim.hook.spawner;

import me.angeschossen.upgradeablespawners.api.events.spawner.SpawnerCreateEvent;
import me.angeschossen.upgradeablespawners.api.events.spawner.SpawnerDeleteEvent;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.ClaimSpawner;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class UpgradeableSpawners implements ClaimSpawner {

    public UpgradeableSpawners(){
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onSpawnerPlace(SpawnerCreateEvent e){
                if (e.getSpawnerPlayer() == null) return;
                if (!availableArea(e.getSpawnerPlayer().getPlayer(), e.getSpawner().getCoordinate().toLocation())){
                    e.setCancelled(true);
                    sendMessageToPlayer("YOU_CANT_PLACE_SPAWNER", e.getSpawnerPlayer().getPlayer());
                }
            }

            @EventHandler
            public void onSpawnerPlace(SpawnerDeleteEvent e){
                if (e.getSpawnerPlayer() == null) return;
                if (!availableArea(e.getSpawnerPlayer().getPlayer(), e.getSpawner().getCoordinate().toLocation())){
                    e.setCancelled(true);
                    sendMessageToPlayer("YOU_CANT_BREAK_SPAWNER", e.getSpawnerPlayer().getPlayer());
                }
            }
        }, RClaim.getInstance());
    }

    @Override
    public String getName() {
        return "UpgradeableSpawners";
    }
}
