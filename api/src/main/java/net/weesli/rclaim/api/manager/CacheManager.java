package net.weesli.rclaim.api.manager;

import net.weesli.rclaim.api.cache.Cache;
import net.weesli.rclaim.api.model.Claim;

public interface CacheManager {
    Cache<String, Claim> getClaims();
}
