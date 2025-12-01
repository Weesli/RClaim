package net.weesli.rclaim.hook.other;

import dev.rosewood.rosestacker.event.BlockStackEvent;
import dev.rosewood.rosestacker.event.BlockUnstackEvent;
import dev.rosewood.rosestacker.event.SpawnerStackEvent;
import dev.rosewood.rosestacker.event.SpawnerUnstackEvent;
import net.weesli.rclaim.api.RClaimProvider;
import net.weesli.rclaim.api.model.Claim;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class HRoseStacker implements Listener {

    public HRoseStacker(Plugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    private boolean availableArea(Player player, Location location) {
        Chunk chunk = location.getChunk();
        Claim claim = RClaimProvider.getClaimManager().getClaim(chunk);
        if (claim == null) return false;
        return claim.isOwner(player.getUniqueId());
    }

    @EventHandler
    public void blockStack(BlockStackEvent event) {
        if (!availableArea(event.getPlayer(), event.getStack().getLocation())) {
            event.setCancelled(true);
            sendMessageToPlayer("YOU_CANT_STACK_BLOCKS", event.getPlayer());
        }
    }

    @EventHandler
    public void blockUnstack(BlockUnstackEvent event) {
        if (event.getPlayer() == null) return;
        if (!availableArea(event.getPlayer(), event.getStack().getLocation())) {
            event.setCancelled(true);
            sendMessageToPlayer("YOU_CANT_STACK_BLOCKS", event.getPlayer());
        }
    }

    @EventHandler
    public void SpawnerStack(SpawnerStackEvent event) {
        if (!availableArea(event.getPlayer(), event.getStack().getLocation())) {
            event.setCancelled(true);
            sendMessageToPlayer("YOU_CANT_STACK_SPAWNERS", event.getPlayer());
        }
    }

    @EventHandler
    public void spawnerUnstack(SpawnerUnstackEvent event) {
        if (event.getPlayer() == null) return;
        if (!availableArea(event.getPlayer(), event.getStack().getLocation())) {
            event.setCancelled(true);
            sendMessageToPlayer("YOU_CANT_STACK_SPAWNERS", event.getPlayer());
        }
    }
}
