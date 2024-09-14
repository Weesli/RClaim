package net.weesli.rClaim.hooks.minion;


import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ClaimMinion {

    boolean checkArea(Player player, Location location);
    String getName();
}
