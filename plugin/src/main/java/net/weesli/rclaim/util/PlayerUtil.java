package net.weesli.rclaim.util;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.UUID;

public class PlayerUtil {

    public static OfflinePlayer getPlayer(String username) {
        return Bukkit.getOfflinePlayerIfCached(username);
    }

    public static @NotNull OfflinePlayer getPlayer(UUID uuid) {
        return Bukkit.getOfflinePlayer(uuid);
    }



}
