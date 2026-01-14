package com.portfolio.infrastructure.config.cache;

import com.github.benmanes.caffeine.cache.Caffeine;
import com.portfolio.shared.constant.CacheName;
import java.time.Duration;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.caffeine.CaffeineCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Multi-level cache configuration (L1: Caffeine, L2: Redis)
 */
@Configuration
public class CacheConfig {

    @Value("${cache.caffeine.spec:maximumSize=1000,expireAfterWrite=5m}")
    private String caffeineSpec;

    @Value("${cache.redis.time-to-live:30m}")
    private Duration redisTtl;

    /**
     * Configure L1 cache with Caffeine (local in-memory cache)
     * @return CaffeineCacheManager
     */
    @Bean
    public CaffeineCacheManager caffeineCacheManager() {
        CaffeineCacheManager cacheManager = new CaffeineCacheManager();

        cacheManager.setCaffeine(Caffeine.newBuilder()
                .maximumSize(1000)
                .expireAfterWrite(5, TimeUnit.MINUTES)
                .recordStats());

        cacheManager.setCacheNames(Arrays.asList(
                CacheName.PRODUCTS,
                CacheName.PRODUCT_BY_ID,
                CacheName.CATEGORIES,
                CacheName.CATEGORY_BY_ID,
                CacheName.USERS,
                CacheName.USER_BY_ID,
                CacheName.ALGORITHM_RESULT,
                CacheName.QUERY_RESULT
        ));

        return cacheManager;
    }

    /**
     * Configure L2 cache with Redis (distributed cache)
     * @param connectionFactory Redis connection factory
     * @return RedisCacheManager
     */
    @Bean
    public RedisCacheManager redisCacheManager(RedisConnectionFactory connectionFactory) {
        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(redisTtl)
                .serializeKeysWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new StringRedisSerializer()))
                .serializeValuesWith(RedisSerializationContext.SerializationPair
                        .fromSerializer(new GenericJackson2JsonRedisSerializer()))
                .disableCachingNullValues();

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withCacheConfiguration(CacheName.PRODUCTS,
                        defaultConfig.entryTtl(Duration.ofMinutes(30)))
                .withCacheConfiguration(CacheName.CATEGORIES,
                        defaultConfig.entryTtl(Duration.ofHours(1)))
                .withCacheConfiguration(CacheName.USERS,
                        defaultConfig.entryTtl(Duration.ofMinutes(15)))
                .withCacheConfiguration(CacheName.ALGORITHM_RESULT,
                        defaultConfig.entryTtl(Duration.ofMinutes(10)))
                .withCacheConfiguration(CacheName.QUERY_RESULT,
                        defaultConfig.entryTtl(Duration.ofMinutes(5)))
                .build();
    }

    /**
     * Primary cache manager - uses multi-level caching
     * @param caffeineCacheManager L1 cache manager
     * @param redisCacheManager L2 cache manager
     * @return MultiLevelCacheManager
     */
    @Bean
    @Primary
    public CacheManager cacheManager(
            CaffeineCacheManager caffeineCacheManager,
            RedisCacheManager redisCacheManager) {
        return new MultiLevelCacheManager(caffeineCacheManager, redisCacheManager);
    }
}
