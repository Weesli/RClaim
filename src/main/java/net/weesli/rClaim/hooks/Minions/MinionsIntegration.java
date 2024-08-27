package net.weesli.rClaim.hooks.Minions;


import net.weesli.rClaim.RClaim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;

public abstract class MinionsIntegration {

    public abstract boolean checkArea(Player player, Location location);
    public abstract String getName();
}
