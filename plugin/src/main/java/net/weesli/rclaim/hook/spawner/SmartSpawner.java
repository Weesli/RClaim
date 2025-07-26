package net.weesli.rclaim.hook.spawner;

import github.nighter.smartspawner.api.events.SpawnerPlayerBreakEvent;
import net.weesli.rclaim.RClaim;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class SmartSpawner implements IClaimSpawner {


    public SmartSpawner() {
        RClaim.getInstance().getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onBreakSpawner(SpawnerPlayerBreakEvent event) {
                Player player = event.getPlayer();
                if (!availableArea(player, event.getLocation())) {
                    event.setCancelled(true);
                }
            }

            }, RClaim.getInstance());
    }

    @Override
    public String getName() {
        return "";
    }
}
