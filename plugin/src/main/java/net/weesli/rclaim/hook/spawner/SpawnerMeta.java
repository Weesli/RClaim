package net.weesli.rclaim.hook.spawner;


import mc.rellox.spawnermeta.api.APIInstance;
import mc.rellox.spawnermeta.api.events.SpawnerPlaceEvent;
import net.weesli.rclaim.RClaim;
import org.bukkit.scheduler.BukkitRunnable;

public class SpawnerMeta implements IClaimSpawner {

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
                event.getPlayer().sendMessage(RClaim.getInstance().getMessage("YOU_CANT_PLACE_SPAWNER"));
            }
        });
    }

    @Override
    public String getName() {
        return "SpawnerMeta";
    }
}
