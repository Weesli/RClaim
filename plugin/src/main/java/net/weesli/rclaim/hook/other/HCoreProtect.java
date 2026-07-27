package net.weesli.rclaim.hook.other;

import net.coreprotect.CoreProtect;
import net.coreprotect.CoreProtectAPI;
import net.weesli.rclaim.RClaim;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.model.SubClaim;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;

/**
 * CoreProtect integration for /claim rollback.
 *
 * NOTE: written from memory of CoreProtect's public API (net.coreprotect.CoreProtectAPI:
 * getAPI(), isEnabled(), APIVersion(), performRollback(...)). It could not be compiled or
 * verified against a real CoreProtect jar in this environment — outbound access to every
 * Maven repo (including the one CoreProtect would be pulled from) was blocked by network
 * policy. The dependency coordinate in build.gradle.kts, the performRollback parameter list/
 * order, and the restriction-list format are all unverified. Treat this integration as
 * untested until built and smoke-tested against a real CoreProtect install.
 */
public class HCoreProtect {

    private static CoreProtectAPI api;

    private HCoreProtect() {
    }

    public static boolean setup() {
        Plugin plugin = Bukkit.getPluginManager().getPlugin("CoreProtect");
        if (!(plugin instanceof CoreProtect coreProtect)) return false;

        CoreProtectAPI coreProtectApi = coreProtect.getAPI();
        if (coreProtectApi == null || !coreProtectApi.isEnabled() || coreProtectApi.APIVersion() < 9) return false;

        api = coreProtectApi;
        return true;
    }

    public static boolean isAvailable() {
        return api != null;
    }

    /**
     * Rolls back every chunk the claim covers (its main chunk plus any subclaims), one
     * chunk at a time, each dispatched on that chunk's own region via FoliaLib so a single
     * rollback request never touches the world synchronously from one thread.
     */
    public static void rollbackClaim(Claim claim, long seconds) {
        if (api == null) return;
        World world = Bukkit.getWorld(claim.getWorldName());
        if (world == null) return;

        rollbackChunk(world, claim.getX(), claim.getZ(), seconds);
        for (SubClaim sub : claim.getSubClaims()) {
            rollbackChunk(world, sub.getX() << 4, sub.getZ() << 4, seconds);
        }
    }

    private static void rollbackChunk(World world, int blockX, int blockZ, long seconds) {
        Location center = new Location(world, blockX + 8, 64, blockZ + 8);
        RClaim.getInstance().getFoliaLib().getScheduler().runAtLocation(center, (task) -> {
            List<String[]> restrictions = new ArrayList<>();
            api.performRollback(seconds, restrictions, null, 8, center, true);
        });
    }
}
