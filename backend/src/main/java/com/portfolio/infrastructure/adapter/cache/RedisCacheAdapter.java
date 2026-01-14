package com.portfolio.infrastructure.adapter.cache;

import com.portfolio.application.port.output.CachePort;
import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

/**
 * Redis cache adapter implementing CachePort interface
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class RedisCacheAdapter implements CachePort {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration DEFAULT_TTL = Duration.ofMinutes(30);

    /**
     * Get value from Redis cache
     * @param key Cache key
     * @param type Value type class
     * @param <T> Value type
     * @return Optional containing cached value if present
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> Optional<T> get(String key, Class<T> type) {
        try {
            Object value = redisTemplate.opsForValue().get(key);

            if (value == null) {
                log.debug("Cache MISS for key: {}", key);
                return Optional.empty();
            }

            log.debug("Cache HIT for key: {}", key);
            return Optional.of((T) value);
        } catch (Exception e) {
            log.error("Error getting value from cache for key: {}", key, e);
            return Optional.empty();
        }
    }

    /**
     * Put value in Redis cache with default TTL
     * @param key Cache key
     * @param value Value to cache
     * @param <T> Value type
     */
    @Override
    public <T> void put(String key, T value) {
        put(key, value, DEFAULT_TTL);
    }

    /**
     * Put value in Redis cache with custom TTL
     * @param key Cache key
     * @param value Value to cache
     * @param ttl Time to live duration
     * @param <T> Value type
     */
    @Override
    public <T> void put(String key, T value, Duration ttl) {
        try {
            redisTemplate.opsForValue().set(key, value, ttl.toMillis(), TimeUnit.MILLISECONDS);
            log.debug("Cache PUT for key: {} with TTL: {}", key, ttl);
        } catch (Exception e) {
            log.error("Error putting value in cache for key: {}", key, e);
        }
    }

    /**
     * Remove value from Redis cache
     * @param key Cache key
     */
    @Override
    public void evict(String key) {
        try {
            redisTemplate.delete(key);
            log.debug("Cache EVICT for key: {}", key);
        } catch (Exception e) {
            log.error("Error evicting value from cache for key: {}", key, e);
        }
    }

    /**
     * Remove all values matching pattern
     * @param pattern Key pattern (supports wildcards)
     */
    @Override
    public void evictByPattern(String pattern) {
        try {
            Set<String> keySet = redisTemplate.keys(pattern);

            if (keySet != null && !keySet.isEmpty()) {
                redisTemplate.delete(keySet);
                log.debug("Cache EVICT by pattern: {}, deleted {} keys", pattern, keySet.size());
            }
        } catch (Exception e) {
            log.error("Error evicting values by pattern: {}", pattern, e);
        }
    }

    /**
     * Check if key exists in cache
     * @param key Cache key
     * @return true if key exists
     */
    @Override
    public boolean exists(String key) {
        try {
            Boolean isExists = redisTemplate.hasKey(key);
            return Boolean.TRUE.equals(isExists);
        } catch (Exception e) {
            log.error("Error checking key existence for: {}", key, e);
            return false;
        }
    }

    /**
     * Clear all cache entries (use with caution)
     */
    @Override
    public void clear() {
        try {
            Set<String> keySet = redisTemplate.keys("*");

            if (keySet != null && !keySet.isEmpty()) {
                redisTemplate.delete(keySet);
                log.warn("Cache CLEAR: deleted {} keys", keySet.size());
            }
        } catch (Exception e) {
            log.error("Error clearing cache", e);
        }
    }
}
