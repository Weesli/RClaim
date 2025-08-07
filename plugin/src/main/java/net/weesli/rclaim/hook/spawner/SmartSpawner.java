package net.weesli.rclaim.hook.spawner;

import github.nighter.smartspawner.api.events.SpawnerPlayerBreakEvent;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.ClaimSpawner;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class SmartSpawner implements ClaimSpawner {


    public SmartSpawner() {
        RClaim.getInstance().getServer().getPluginManager().registerEvents(new Listener() {

            @EventHandler
            public void onBreakSpawner(SpawnerPlayerBreakEvent event) {
                Player player = event.getPlayer();
                if (!availableArea(player, event.getLocation())) {
                    event.setCancelled(true);
                    sendMessageToPlayer("YOU_CANT_PLACE_SPAWNER", event.getPlayer());
                }
            }

            }, RClaim.getInstance());
    }

    @Override
    public String getName() {
        return "";
    }
}
