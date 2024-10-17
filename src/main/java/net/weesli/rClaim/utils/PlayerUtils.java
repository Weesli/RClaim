package net.weesli.rClaim.utils;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import java.util.UUID;

/**
 * PlayerUtils provides utility methods for handling players, both online and offline, using their username or UUID.
 */
public class PlayerUtils {

    /**
     * Retrieves an OfflinePlayer by their username.
     *
     * @param username The username of the player.
     * @return The OfflinePlayer object if the player has played before, otherwise null.
     */
    public static OfflinePlayer getPlayer(String username) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
        if (offlinePlayer.hasPlayedBefore()) {
            return offlinePlayer;
        }
        return null;
    }

    /**
     * Retrieves an OfflinePlayer by their UUID.
     *
     * @param uuid The UUID of the player.
     * @return The OfflinePlayer object if the player has played before, otherwise null.
     */
    public static OfflinePlayer getPlayer(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        if (offlinePlayer.hasPlayedBefore()) {
            return offlinePlayer;
        }
        return null;
    }

    /**
     * Checks if a player is currently online by their username.
     *
     * @param username The username of the player.
     * @return True if the player is online, otherwise false.
     */
    public static boolean isPlayerOnline(String username) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(username);
        return offlinePlayer.isOnline();
    }

    /**
     * Checks if a player is currently online by their UUID.
     *
     * @param uuid The UUID of the player.
     * @return True if the player is online, otherwise false.
     */
    public static boolean isPlayerOnline(UUID uuid) {
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(uuid);
        return offlinePlayer.isOnline();
    }
}
