package net.weesli.rClaim.hooks.Spawners;


import org.bukkit.Location;
import org.bukkit.entity.Player;

public abstract class SpawnerIntegration {

    abstract boolean checkArea(Player player,Location location);

    public abstract String getName();
}
