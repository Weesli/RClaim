package net.weesli.rclaim.hook.minion;

import me.jet315.minions.events.PreMinionPlaceEvent;
import net.weesli.rclaim.RClaim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class JetMinions implements Listener, IClaimMinion{

    public JetMinions() {
        Bukkit.getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    @EventHandler
    public void onPlaceMinion(PreMinionPlaceEvent e){
        Player player = e.getPlayer();
        Location location = e.getMinionLocation();
        if (!availableArea(player, location)){
            e.setCancelled(true);
            player.sendMessage(RClaim.getInstance().getMessage("YOU_CANT_PLACE_MINION"));
        }
    }

    @Override
    public String getName() {
        return "JetMinions";
    }
}
