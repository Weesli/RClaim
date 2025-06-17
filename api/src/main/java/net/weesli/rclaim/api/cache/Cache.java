package net.weesli.rclaim.api.cache;

import java.util.Map;

public interface Cache<T,V> {
    V get(T key);
    Map<T, V> getCache();
}
