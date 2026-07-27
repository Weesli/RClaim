package net.weesli.rclaim.manager;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the pending /claim rollback confirmation per player (claim + duration), so
 * "/claim rollback confirm" knows exactly what the preceding "/claim rollback <duration>"
 * call asked for instead of silently trusting a bare "confirm".
 */
public final class PendingRollbackManager {

    private static final long CONFIRM_WINDOW_MILLIS = 30_000L;
    private static final Map<UUID, Pending> pending = new ConcurrentHashMap<>();

    private PendingRollbackManager() {
    }

    public static void request(UUID player, String claimId, long seconds) {
        pending.put(player, new Pending(claimId, seconds, System.currentTimeMillis() + CONFIRM_WINDOW_MILLIS));
    }

    /**
     * Removes and returns the pending request if it exists, matches the given claim, and
     * hasn't expired; null otherwise.
     */
    public static Pending consume(UUID player, String claimId) {
        Pending request = pending.remove(player);
        if (request == null) return null;
        if (request.expiresAtMillis() < System.currentTimeMillis()) return null;
        if (!request.claimId().equals(claimId)) return null;
        return request;
    }

    public record Pending(String claimId, long seconds, long expiresAtMillis) {
    }
}
