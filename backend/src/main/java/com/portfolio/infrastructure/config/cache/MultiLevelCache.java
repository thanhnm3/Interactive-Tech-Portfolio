package com.portfolio.infrastructure.config.cache;

import java.util.concurrent.Callable;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.Cache;

/**
 * Multi-level cache implementation (L1: local, L2: distributed)
 * Read: L1 -> L2 -> Source
 * Write: L1 + L2
 */
@RequiredArgsConstructor
@Slf4j
public class MultiLevelCache implements Cache {

    private final String name;
    private final Cache l1Cache;  // Local (Caffeine)
    private final Cache l2Cache;  // Distributed (Redis)

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Object getNativeCache() {
        return this;
    }

    /**
     * Get value from cache (L1 first, then L2)
     * @param key Cache key
     * @return ValueWrapper containing cached value or null
     */
    @Override
    public ValueWrapper get(Object key) {
        // Try L1 first
        if (l1Cache != null) {
            ValueWrapper l1Value = l1Cache.get(key);

            if (l1Value != null) {
                log.debug("Cache HIT [L1] for key: {}", key);
                return l1Value;
            }
        }

        // Try L2
        if (l2Cache != null) {
            ValueWrapper l2Value = l2Cache.get(key);

            if (l2Value != null) {
                log.debug("Cache HIT [L2] for key: {}", key);

                // Populate L1 from L2
                if (l1Cache != null && l2Value.get() != null) {
                    l1Cache.put(key, l2Value.get());
                }
                return l2Value;
            }
        }

        log.debug("Cache MISS for key: {}", key);
        return null;
    }

    /**
     * Get value with type from cache
     * @param key Cache key
     * @param type Expected value type
     * @param <T> Value type
     * @return Cached value or null
     */
    @Override
    public <T> T get(Object key, Class<T> type) {
        ValueWrapper wrapper = get(key);

        if (wrapper == null) {
            return null;
        }

        Object value = wrapper.get();

        if (value != null && type != null && !type.isInstance(value)) {
            throw new IllegalStateException(
                    "Cached value is not of required type [" + type.getName() + "]: " + value);
        }

        return type.cast(value);
    }

    /**
     * Get value or load from callable if not cached
     * @param key Cache key
     * @param valueLoader Callable to load value if not cached
     * @param <T> Value type
     * @return Cached or loaded value
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> T get(Object key, Callable<T> valueLoader) {
        ValueWrapper wrapper = get(key);

        if (wrapper != null) {
            return (T) wrapper.get();
        }

        try {
            T value = valueLoader.call();
            put(key, value);
            return value;
        } catch (Exception e) {
            throw new ValueRetrievalException(key, valueLoader, e);
        }
    }

    /**
     * Put value in both L1 and L2 caches
     * @param key Cache key
     * @param value Value to cache
     */
    @Override
    public void put(Object key, Object value) {
        log.debug("Cache PUT for key: {}", key);

        if (l1Cache != null) {
            l1Cache.put(key, value);
        }

        if (l2Cache != null) {
            l2Cache.put(key, value);
        }
    }

    /**
     * Put value only if absent
     * @param key Cache key
     * @param value Value to cache
     * @return Existing value wrapper or null
     */
    @Override
    public ValueWrapper putIfAbsent(Object key, Object value) {
        ValueWrapper existingValue = get(key);

        if (existingValue == null) {
            put(key, value);
            return null;
        }

        return existingValue;
    }

    /**
     * Evict value from both caches
     * @param key Cache key
     */
    @Override
    public void evict(Object key) {
        log.debug("Cache EVICT for key: {}", key);

        if (l1Cache != null) {
            l1Cache.evict(key);
        }

        if (l2Cache != null) {
            l2Cache.evict(key);
        }
    }

    /**
     * Clear both caches
     */
    @Override
    public void clear() {
        log.debug("Cache CLEAR for: {}", name);

        if (l1Cache != null) {
            l1Cache.clear();
        }

        if (l2Cache != null) {
            l2Cache.clear();
        }
    }
}
