package net.weesli.rclaim.cache;

import lombok.Getter;
import net.weesli.rclaim.api.cache.Cache;

import java.util.Map;
@Getter
public class CacheImpl<T, V> implements Cache<T, V> {
    private final Map<T, V> cache;

    public CacheImpl(Map<T, V> cache) {
        this.cache = cache;
    }
    public V get(T key) {
        return cache.get(key);
    }

    public Map<T, V> getCache() {
        return cache;
    }
}
