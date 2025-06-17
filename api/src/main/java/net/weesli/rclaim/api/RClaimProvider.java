package net.weesli.rclaim.api;

import lombok.Getter;
import net.weesli.rclaim.api.manager.CacheManager;
import net.weesli.rclaim.api.manager.ClaimManager;
import net.weesli.rclaim.api.manager.TagManager;
import org.bukkit.plugin.Plugin;

/**
 * A class that provides access to the RClaim API.
 *
 */
public final class RClaimProvider {
    @Getter private static Plugin plugin;
    @Getter private static ClaimManager claimManager;
    @Getter private static CacheManager cacheManager;
    @Getter private static TagManager tagManager;

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
}
