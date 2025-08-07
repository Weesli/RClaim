package net.weesli.rclaim.api;

import lombok.Getter;
import net.weesli.rclaim.api.database.ClaimDatabase;
import net.weesli.rclaim.api.hook.manager.*;
import net.weesli.rclaim.api.manager.CacheManager;
import net.weesli.rclaim.api.manager.ClaimManager;
import net.weesli.rclaim.api.manager.TagManager;
import org.bukkit.plugin.Plugin;

import java.util.logging.Level;

/**
 * A class that provides access to the RClaim API.
 *
 */
public final class RClaimProvider {
    @Getter private static Plugin plugin;
    @Getter private static ClaimManager claimManager;
    @Getter private static CacheManager cacheManager;
    @Getter private static TagManager tagManager;

    // hooks managers
    @Getter private static HologramManager hologramManager;
    @Getter private static SpawnerManager spawnerManager;
    @Getter private static EconomyManager economyManager;
    @Getter private static CombatManager combatManager;
    @Getter private static MinionsManager minionsManager;

    // database
    @Getter private static ClaimDatabase storage;

    public RClaimProvider(){}

    public static void setClaimManager(ClaimManager manager) {
        if (claimManager != null){
            throw new IllegalArgumentException("ClaimManager is already set!");
        }
        claimManager = manager;
    }

    public static void setCacheManager(CacheManager manager) {
        if (cacheManager != null){
            throw new IllegalArgumentException("CacheManager is already set!");
        }
        cacheManager = manager;
    }

    public static void setTagManager(TagManager manager) {
        if (tagManager != null){
            throw new IllegalArgumentException("TagManager is already set!");
        }
        tagManager = manager;
    }

    public static void setPlugin(Plugin plugin) {
        if (RClaimProvider.plugin != null){
            throw new IllegalArgumentException("Plugin is already set!");
        }
        RClaimProvider.plugin = plugin;
    }

    public static void setHologramManager(HologramManager manager) {
        if (hologramManager != null){
            throw new IllegalArgumentException("HologramManager is already set!");
        }
        hologramManager = manager;
    }

    public static void setSpawnerManager(SpawnerManager manager) {
        if (spawnerManager != null){
            throw new IllegalArgumentException("SpawnerManager is already set!");
        }
        spawnerManager = manager;
    }

    public static void setEconomyManager(EconomyManager manager) {
        if (economyManager != null){
            throw new IllegalArgumentException("EconomyManager is already set!");
        }
        economyManager = manager;
    }

    public static void setCombatManager(CombatManager manager) {
        if (combatManager != null){
            throw new IllegalArgumentException("CombatManager is already set!");
        }
        combatManager = manager;
    }

    public static void setMinionsManager(MinionsManager manager) {
        if (minionsManager != null){
            throw new IllegalArgumentException("MinionsManager is already set!");
        }
        minionsManager = manager;
    }

    public static void setStorage(ClaimDatabase x) {
        if (storage != null){
            throw new IllegalArgumentException("Storage is already set!");
        }
        storage = x;
    }
}
