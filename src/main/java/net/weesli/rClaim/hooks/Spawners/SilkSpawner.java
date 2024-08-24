package net.weesli.rClaim.hooks.Spawners;

import de.corneliusmay.silkspawners.plugin.events.SpawnerPlaceEvent;
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

public class SilkSpawner extends SpawnerIntegration {

    public SilkSpawner(){
        Bukkit.getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onPlaceSpawner(SpawnerPlaceEvent event){
                if (!checkArea(event.getPlayer(), event.getLocation())){
                    event.setCancelled(true);
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
