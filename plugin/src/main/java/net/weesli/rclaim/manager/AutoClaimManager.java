package net.weesli.rclaim.manager;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public final class AutoClaimManager {

    private static final Set<UUID> enabled = ConcurrentHashMap.newKeySet();

    private AutoClaimManager() {
    }

    public static boolean toggle(UUID uuid) {
        if (enabled.remove(uuid)) {
            return false;
        }
        enabled.add(uuid);
        return true;
    }

    public static boolean isEnabled(UUID uuid) {
        return enabled.contains(uuid);
    }

    public static void disable(UUID uuid) {
        enabled.remove(uuid);
    }
}
