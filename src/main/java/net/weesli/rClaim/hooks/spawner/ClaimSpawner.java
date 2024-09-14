package net.weesli.rClaim.hooks.spawner;


import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface ClaimSpawner {

    public boolean checkArea(Player player,Location location);

    String getName();
}
