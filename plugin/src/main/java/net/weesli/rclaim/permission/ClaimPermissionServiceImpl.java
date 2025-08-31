package net.weesli.rclaim.permission;

import net.weesli.rclaim.api.permission.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class ClaimPermissionServiceImpl implements ClaimPermissionService {

    private final Plugin plugin;
    private final Map<String, ClaimPermissionRegistry> permissions = new HashMap<>();

    public ClaimPermissionServiceImpl(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean isAllowed(String permissionKey, Player player, Location loc) {
        ClaimPermissionRegistry reg = permissions.get(permissionKey);
        if (reg == null) return false;
        return reg.internalCheck(player, loc);
    }

    @Override
    public void registerPermission(ClaimPermissionRegistry permission) {
        permissions.put(permission.key(), permission);
        Bukkit.getPluginManager().registerEvents(permission, plugin);
    }

    @Override
    public Map<String, ClaimPermissionRegistry> getRegistries() {
        return permissions;
    }
}
