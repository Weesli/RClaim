package net.weesli.rclaim.api.status;

import net.weesli.rclaim.api.model.Claim;
import org.bukkit.event.Listener;

/**
 * It is an API class for external status checks.<br>
 * To create a custom status, you must {@code extend} this class to a class you have created yourself.<br>
 * <p>
 * Example code:
 * <blockquote><pre>
 * public class MyClaimStatusRegistry extends ClaimStatusRegistry {
 *
 *   public MyClaimStatusRegistry() {
 *       super("my-claim-status");
 *   }
 *
 *   // if you want check another status {@code Override internalCheck} method,
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
 * last step is to register this class to {@link ClaimStatusService}
 * <blockquote><pre>
 *     RClaimProvider.getStatusService().register(new MyClaimStatusRegistry());
 * </pre></blockquote>
 */

public class ClaimStatusRegistry implements Listener {
    private final String key;

    protected ClaimStatusRegistry(String key) {
        this.key = key;
    }

    public String key() {
        return key;
    }

    public boolean internalCheck(Claim claim) {
        return claim == null || claim.checkStatus(key);
    }
}
