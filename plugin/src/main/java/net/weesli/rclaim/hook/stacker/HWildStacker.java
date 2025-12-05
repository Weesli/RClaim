package net.weesli.rclaim.hook.stacker;

import com.bgsoftware.wildstacker.api.events.*;
import dev.rosewood.rosestacker.event.BlockStackEvent;
import dev.rosewood.rosestacker.event.BlockUnstackEvent;
import dev.rosewood.rosestacker.event.SpawnerStackEvent;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.ClaimStacker;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class HWildStacker implements Listener, ClaimStacker {

    public HWildStacker(RClaim plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void blockStack(BarrelPlaceEvent event) {
        if (!availableArea(event.getPlayer(), event.getBarrel().getLocation())) {
            event.setCancelled(true);
            sendMessageToPlayer("YOU_CANT_STACK_BLOCKS", event.getPlayer());
        }
    }

    @EventHandler
    public void blockUnstack(BarrelUnstackEvent event) {
        if (event.getUnstackSource() == null) return;
        if (!(event.getUnstackSource() instanceof Player player)) return;
        if (!availableArea(player, event.getBarrel().getLocation())) {
            event.setCancelled(true);
            sendMessageToPlayer("YOU_CANT_UNSTACK_BLOCKS", player);
        }
    }

    @EventHandler
    public void SpawnerStack(SpawnerPlaceEvent event) {
        if (!availableArea(event.getPlayer(), event.getSpawner().getLocation())) {
            event.setCancelled(true);
            sendMessageToPlayer("YOU_CANT_STACK_SPAWNERS", event.getPlayer());
        }
    }

    @EventHandler
    public void spawnerUnstack(SpawnerUnstackEvent event) {
        if (event.getUnstackSource() == null) return;
        if (!(event.getUnstackSource() instanceof Player player)) return;
        if (!availableArea(player, event.getSpawner().getLocation())) {
            event.setCancelled(true);
            sendMessageToPlayer("YOU_CANT_UNSTACK_BLOCKS", player);
        }
    }
}
