package net.weesli.rClaim.hooks.Minions;

import me.jet315.minions.events.PostMinionPlaceEvent;
import me.jet315.minions.events.PreMinionPlaceEvent;
import net.weesli.rClaim.RClaim;
import net.weesli.rClaim.management.ClaimManager;
import net.weesli.rClaim.utils.Claim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class JetMinions extends MinionsIntegration implements Listener{

    public JetMinions() {
        Bukkit.getPluginManager().registerEvents(this, RClaim.getInstance());
    }

    @EventHandler
    public void onPlaceMinion(PreMinionPlaceEvent e){
        Player player = e.getPlayer();
        Location location = e.getMinionLocation();
        if (!checkArea(player, location)){
            e.setCancelled(true);
            player.sendMessage(RClaim.getInstance().getMessage("YOU_CANT_PLACE_MINION"));
        }
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

    @Override
    public String getName() {
        return "JetMinions";
    }
}
