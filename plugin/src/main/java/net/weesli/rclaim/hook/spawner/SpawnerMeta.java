package net.weesli.rclaim.hook.spawner;


import com.tcoded.folialib.FoliaLib;
import mc.rellox.spawnermeta.api.APIInstance;
import mc.rellox.spawnermeta.api.events.SpawnerPlaceEvent;
import net.weesli.rclaim.api.hook.ClaimSpawner;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class SpawnerMeta implements ClaimSpawner {

    APIInstance api = mc.rellox.spawnermeta.SpawnerMeta.instance().getAPI();

    public SpawnerMeta(FoliaLib foliaLib) {
        api.register(SpawnerPlaceEvent.class, event -> {
            if (!availableArea(event.getPlayer(), event.getBlock().getLocation())) {
                foliaLib.getScheduler().runAtLocationTimer(event.getBlock().getLocation(), task -> {
                    api.breakSpawner(event.getBlock(), true);
                    task.cancel();
                }, 3, 3);
                sendMessageToPlayer("YOU_CANT_PLACE_SPAWNER", event.getPlayer());
            }
        });
    }

    @Override
    public String getName() {
        return "SpawnerMeta";
    }
}
