package net.weesli.rclaim.hook.minion;

import me.waterarchery.litminions.api.events.MinionPlaceEvent;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.hook.ClaimMinion;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import static net.weesli.rclaim.config.lang.LangConfig.sendMessageToPlayer;

public class LitMinions implements Listener, ClaimMinion {

    public LitMinions(){
        Bukkit.getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    @EventHandler
    public void placeMinion(MinionPlaceEvent e){
        Player player = e.getPlayer();
        Location location = e.getLocation();
        if (!availableArea(player, location)){
            e.setCancelled(true);
            sendMessageToPlayer("YOU_CANT_PLACE_MINION", player);
        }
    }

    @Override
    public String getName() {
        return "LitMinions";
    }
}
