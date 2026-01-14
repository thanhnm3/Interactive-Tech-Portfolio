package com.portfolio.infrastructure.config.cache;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

/**
 * Multi-level cache manager combining L1 (Caffeine) and L2 (Redis) caches
 */
@RequiredArgsConstructor
public class MultiLevelCacheManager implements CacheManager {

    private final CacheManager l1CacheManager;  // Caffeine
    private final CacheManager l2CacheManager;  // Redis

    /**
     * Get multi-level cache by name
     * @param name Cache name
     * @return MultiLevelCache wrapping L1 and L2 caches
     */
    @Override
    public Cache getCache(String name) {
        Cache l1Cache = l1CacheManager.getCache(name);
        Cache l2Cache = l2CacheManager.getCache(name);

        if (l1Cache == null && l2Cache == null) {
            return null;
        }

        return new MultiLevelCache(name, l1Cache, l2Cache);
    }

    /**
     * Get all cache names
     * @return Collection of cache names
     */
    @Override
    public Collection<String> getCacheNames() {
        Set<String> cacheNameSet = new HashSet<>();
        cacheNameSet.addAll(l1CacheManager.getCacheNames());
        cacheNameSet.addAll(l2CacheManager.getCacheNames());
        return Collections.unmodifiableSet(cacheNameSet);
    }
}
