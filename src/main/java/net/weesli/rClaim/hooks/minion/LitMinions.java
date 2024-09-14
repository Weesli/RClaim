package net.weesli.rClaim.hooks.minion;

import me.waterarchery.litminions.api.events.MinionPlaceEvent;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.utils.ClaimManager;
import net.weesli.rClaim.modal.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class LitMinions implements Listener,ClaimMinion {

    public LitMinions(){
        Bukkit.getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    @Override
    public boolean checkArea(Player player, Location location) {
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

    @EventHandler
    public void placeMinion(MinionPlaceEvent e){
        Player player = e.getPlayer();
        Location location = e.getLocation();
        if (!checkArea(player, location)){
            e.setCancelled(true);
            player.sendMessage(RClaim.getInstance().getMessage("YOU_CANT_PLACE_MINION"));
        }
    }

    @Override
    public String getName() {
        return "LitMinions";
    }
}
