package net.weesli.rClaim.hooks.Spawners;


import de.corneliusmay.silkspawners.api.Bukkit;
import mc.rellox.spawnermeta.api.APIInstance;
import mc.rellox.spawnermeta.api.events.SpawnerPlaceEvent;
import mc.rellox.spawnermeta.api.events.SpawnerPreSpawnEvent;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.utils.Claim;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;

public class SpawnerMeta extends SpawnerIntegration {

    APIInstance api = mc.rellox.spawnermeta.SpawnerMeta.instance().getAPI();

    public SpawnerMeta(){
        api.register(SpawnerPlaceEvent.class, event ->{
            if (!checkArea(event.getPlayer(), event.getBlock().getLocation())){
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
    boolean checkArea(Player player, Location location) {
        List<Claim> claim = ClaimManager.getClaims().stream().filter(claim1 -> claim1.isOwner(player.getUniqueId()) || claim1.isMember(player.getUniqueId())).toList();
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
