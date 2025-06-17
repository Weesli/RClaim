package net.weesli.rclaim.hook.minion;

import me.waterarchery.litminions.api.events.MinionPlaceEvent;
import net.weesli.rclaim.RClaim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class LitMinions implements Listener, IClaimMinion {

    public LitMinions(){
        Bukkit.getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    @EventHandler
    public void placeMinion(MinionPlaceEvent e){
        Player player = e.getPlayer();
        Location location = e.getLocation();
        if (!availableArea(player, location)){
            e.setCancelled(true);
            player.sendMessage(RClaim.getInstance().getMessage("YOU_CANT_PLACE_MINION"));
        }
    }

    @Override
    public String getName() {
        return "LitMinions";
    }
}
