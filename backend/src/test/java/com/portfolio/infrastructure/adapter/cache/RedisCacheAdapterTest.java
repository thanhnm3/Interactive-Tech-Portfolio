package com.portfolio.infrastructure.adapter.cache;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RedisCacheAdapter
 */
@DisplayName("RedisCacheAdapter Tests")
class RedisCacheAdapterTest {

    private RedisCacheAdapter adapter;
    private RedisTemplate<String, Object> redisTemplate;
    private ValueOperations<String, Object> valueOperations;

    @BeforeEach
    @SuppressWarnings("unchecked")
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        adapter = new RedisCacheAdapter(redisTemplate);
    }

    @Test
    @DisplayName("Should get value from cache")
    void shouldGetValueFromCache() {
        String key = "test-key";
        String value = "test-value";
        when(valueOperations.get(key)).thenReturn(value);

        Optional<String> result = adapter.get(key, String.class);

        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(value);
    }

    @Test
    @DisplayName("Should return empty when key not found")
    void shouldReturnEmptyWhenKeyNotFound() {
        String key = "non-existent";
        when(valueOperations.get(key)).thenReturn(null);

        Optional<String> result = adapter.get(key, String.class);

        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("Should put value in cache with default TTL")
    void shouldPutValueInCacheWithDefaultTtl() {
        String key = "test-key";
        String value = "test-value";

        adapter.put(key, value);

        verify(valueOperations).set(eq(key), eq(value), anyLong(), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("Should put value in cache with custom TTL")
    void shouldPutValueInCacheWithCustomTtl() {
        String key = "test-key";
        String value = "test-value";
        Duration ttl = Duration.ofMinutes(10);

        adapter.put(key, value, ttl);

        verify(valueOperations).set(eq(key), eq(value), eq(ttl.toMillis()), eq(TimeUnit.MILLISECONDS));
    }

    @Test
    @DisplayName("Should evict key from cache")
    void shouldEvictKeyFromCache() {
        String key = "test-key";

        adapter.evict(key);

        verify(redisTemplate).delete(key);
    }

    @Test
    @DisplayName("Should check if key exists")
    void shouldCheckIfKeyExists() {
        String key = "test-key";
        when(redisTemplate.hasKey(key)).thenReturn(true);

        boolean exists = adapter.exists(key);

        assertThat(exists).isTrue();
    }

    @Test
    @DisplayName("Should clear all cache entries")
    void shouldClearAllCacheEntries() {
        Set<String> keys = Set.of("key1", "key2");
        when(redisTemplate.keys("*")).thenReturn(keys);

        adapter.clear();

        verify(redisTemplate).delete(keys);
    }
}
