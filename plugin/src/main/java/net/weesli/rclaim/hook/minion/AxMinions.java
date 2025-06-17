package net.weesli.rclaim.hook.minion;

import com.artillexstudios.axminions.api.events.PreMinionPlaceEvent;
import net.weesli.rclaim.RClaim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class AxMinions implements Listener, IClaimMinion{

    public AxMinions(){
        Bukkit.getServer().getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    @EventHandler
    public void onPlaceMinions(PreMinionPlaceEvent e){
        Player player = e.getPlacer();
        Location location = e.getLocation();
        if (!availableArea(player, location)){
            e.setCancelled(true);
            player.sendMessage(RClaim.getInstance().getMessage("YOU_CANT_PLACE_MINION"));
        }
    }

    @Override
    public String getName() {
        return "AxMinions";
    }
}
