package net.weesli.rClaim.hooks.Spawners;

import me.angeschossen.upgradeablespawners.api.events.spawner.SpawnerCreateEvent;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.utils.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;
import java.util.stream.Collectors;

public class UpgradeableSpawners extends SpawnerIntegration{

    public UpgradeableSpawners(){
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onSpawnerPlace(SpawnerCreateEvent e){
                if (!checkArea(e.getSpawnerPlayer().getPlayer(), e.getSpawner().getCoordinate().toLocation())){
                    e.setCancelled(true);
                    e.getSpawnerPlayer().getPlayer().sendMessage(RClaim.getInstance().getMessage("YOU_CANT_PLACE_SPAWNER"));
                }
            }
        }, RClaim.getInstance());
    }

    @Override
    boolean checkArea(Player player, Location location) {
        List<Claim> claim = ClaimManager.getClaims().stream().filter(claim1 -> claim1.isOwner(player.getUniqueId()) || claim1.isMember(player.getUniqueId())).collect(Collectors.toList());
        if (!claim.isEmpty()){
            for (Claim c : claim){
                if (c.contains(location)){
                    return true;
                }
            }
        }
        return false;
    }
}
