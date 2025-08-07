package net.weesli.rclaim.api.hook.manager;

import net.weesli.rclaim.api.hook.ClaimSpawner;

public interface SpawnerManager {
    ClaimSpawner getIntegration();
}
