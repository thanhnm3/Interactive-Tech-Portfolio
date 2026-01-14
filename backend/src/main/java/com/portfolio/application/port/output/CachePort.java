package com.portfolio.application.port.output;

import java.time.Duration;
import java.util.Optional;

/**
 * Cache port interface for caching operations
 */
public interface CachePort {

    /**
     * Get value from cache
     * @param key Cache key
     * @param type Value type class
     * @param <T> Value type
     * @return Optional containing cached value if present
     */
    <T> Optional<T> get(String key, Class<T> type);

    /**
     * Put value in cache with default TTL
     * @param key Cache key
     * @param value Value to cache
     * @param <T> Value type
     */
    <T> void put(String key, T value);

    /**
     * Put value in cache with custom TTL
     * @param key Cache key
     * @param value Value to cache
     * @param ttl Time to live duration
     * @param <T> Value type
     */
    <T> void put(String key, T value, Duration ttl);

    /**
     * Remove value from cache
     * @param key Cache key
     */
    void evict(String key);

    /**
     * Remove all values matching pattern
     * @param pattern Key pattern (supports wildcards)
     */
    void evictByPattern(String pattern);

    /**
     * Check if key exists in cache
     * @param key Cache key
     * @return true if key exists
     */
    boolean exists(String key);

    /**
     * Clear all cache entries
     */
    void clear();
}
