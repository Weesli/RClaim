package net.weesli.rclaim.api.permission;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.Map;

public interface ClaimPermissionService {
    boolean isAllowed(String permissionKey, Player player, Location loc);

    void registerPermission(ClaimPermissionRegistry permission);

    Map<String, ClaimPermissionRegistry> getRegistries();
}