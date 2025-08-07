package net.weesli.rclaim.hook.spawner;


import mc.rellox.spawnermeta.api.APIInstance;
import mc.rellox.spawnermeta.api.events.SpawnerPlaceEvent;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.ClaimSpawner;
import org.bukkit.scheduler.BukkitRunnable;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class SpawnerMeta implements ClaimSpawner {

    APIInstance api = mc.rellox.spawnermeta.SpawnerMeta.instance().getAPI();

    public SpawnerMeta(){
        api.register(SpawnerPlaceEvent.class, event ->{
            if (!availableArea(event.getPlayer(), event.getBlock().getLocation())){
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        api.breakSpawner(event.getBlock(), true);
                        this.cancel();
                    }
                }.runTaskTimer(RClaim.getInstance(), 3,0);
                sendMessageToPlayer("YOU_CANT_PLACE_SPAWNER", event.getPlayer());
            }
        });
    }

    @Override
    public String getName() {
        return "SpawnerMeta";
    }
}
