package net.weesli.rclaim.api.permission;

import net.weesli.rclaim.api.RClaimProvider;
import net.weesli.rclaim.api.model.Claim;
import net.weesli.rclaim.api.status.ClaimStatusService;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.Listener;

/**
 * It is an API class for external authorization.<br>
 * To create a new authorization, you must {@code extend} this class to a class you have created yourself.<br>
 * <p>
 * Example code:
 * <blockquote><pre>
 * public class MyClaimPermissionRegistry extends  ClaimPermissionRegistry {
 *
 *   public MyClaimPermissionRegistry() {
 *       super("my-claim-permission");
 *   }
 *
 *   // if you want another check permission or another status {@code Override internalCheck} method,
 *   // but I recommend to use default method
 *   EventHandler
 *   public void on(BlockBreakEvent e) {
 *       if (!internalCheck(e.getPlayer(), e.getBlock().getLocation())) {
 *           e.setCancelled(true);
 *           sendMessageToPlayer("PERMISSION_BLOCK_BREAK", e.getPlayer());
 *       }
 *   }
 * }
 * </pre></blockquote>
 *
 * last step is to register this class to {@link ClaimPermissionService}
 * <blockquote><pre>
 *     RClaimProvider.getPermissionService().register(new MyClaimPermissionRegistry());
 * </pre></blockquote>
 */
public abstract class ClaimPermissionRegistry implements Listener {

    private final String key;
    protected final ClaimPermissionService claimService;

    protected ClaimPermissionRegistry(ClaimPermissionService claimService, String key) {
        this.claimService = claimService;
        this.key = key;
    }

    public String key() {
        return key;
    }

    public boolean internalCheck(Player player, Location loc) {
        Claim claim = RClaimProvider.getClaimManager().getClaim(loc);
        if (claim == null) return true;
        if(claim.getOwner().equals(player.getUniqueId())) return true;
        return claim.checkPermission(player.getUniqueId(), key());
    }

    protected void deny(Player player, Event e) {
        if (e instanceof Cancellable) ((Cancellable) e).setCancelled(true);
        onDeny(player, e);
    }

    protected void onDeny(Player player, Event e) {}
}
