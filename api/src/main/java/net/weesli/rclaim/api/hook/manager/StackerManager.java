package net.weesli.rclaim.api.hook.manager;

import net.weesli.rclaim.api.hook.ClaimStacker;

public interface StackerManager {
    ClaimStacker getIntegration();
}
