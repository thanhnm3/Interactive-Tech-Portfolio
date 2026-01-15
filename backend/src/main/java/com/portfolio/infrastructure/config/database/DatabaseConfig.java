package com.portfolio.infrastructure.config.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.util.HashMap;
import java.util.Map;
import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

/**
 * Database configuration with connection pooling and read/write splitting
 */
@Configuration
@ConditionalOnProperty(name = "spring.datasource.primary.url")
public class DatabaseConfig {

    // Primary datasource properties
    @Value("${spring.datasource.primary.url}")
    private String primaryUrl;

    @Value("${spring.datasource.primary.username}")
    private String primaryUsername;

    @Value("${spring.datasource.primary.password}")
    private String primaryPassword;

    @Value("${spring.datasource.primary.driver-class-name}")
    private String primaryDriverClassName;

    @Value("${spring.datasource.primary.hikari.pool-name}")
    private String primaryPoolName;

    @Value("${spring.datasource.primary.hikari.maximum-pool-size}")
    private int primaryMaxPoolSize;

    @Value("${spring.datasource.primary.hikari.minimum-idle}")
    private int primaryMinIdle;

    @Value("${spring.datasource.primary.hikari.idle-timeout}")
    private long primaryIdleTimeout;

    @Value("${spring.datasource.primary.hikari.connection-timeout}")
    private long primaryConnectionTimeout;

    @Value("${spring.datasource.primary.hikari.max-lifetime}")
    private long primaryMaxLifetime;

    // Replica datasource properties (optional, defaults to primary if not configured)
    @Value("${spring.datasource.replica.url:${spring.datasource.primary.url}}")
    private String replicaUrl;

    @Value("${spring.datasource.replica.username:${spring.datasource.primary.username}}")
    private String replicaUsername;

    @Value("${spring.datasource.replica.password:${spring.datasource.primary.password}}")
    private String replicaPassword;

    @Value("${spring.datasource.replica.driver-class-name:${spring.datasource.primary.driver-class-name}}")
    private String replicaDriverClassName;

    @Value("${spring.datasource.replica.hikari.pool-name:${spring.datasource.primary.hikari.pool-name}-replica}")
    private String replicaPoolName;

    @Value("${spring.datasource.replica.hikari.maximum-pool-size:${spring.datasource.primary.hikari.maximum-pool-size}}")
    private int replicaMaxPoolSize;

    @Value("${spring.datasource.replica.hikari.minimum-idle:${spring.datasource.primary.hikari.minimum-idle}}")
    private int replicaMinIdle;

    @Value("${spring.datasource.replica.hikari.idle-timeout:${spring.datasource.primary.hikari.idle-timeout}}")
    private long replicaIdleTimeout;

    @Value("${spring.datasource.replica.hikari.connection-timeout:${spring.datasource.primary.hikari.connection-timeout}}")
    private long replicaConnectionTimeout;

    @Value("${spring.datasource.replica.hikari.max-lifetime:${spring.datasource.primary.hikari.max-lifetime}}")
    private long replicaMaxLifetime;

    /**
     * Create primary (write) datasource with HikariCP
     * @return Primary HikariDataSource
     */
    @Bean
    public DataSource primaryDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(primaryUrl);
        config.setUsername(primaryUsername);
        config.setPassword(primaryPassword);
        config.setDriverClassName(primaryDriverClassName);
        config.setPoolName(primaryPoolName);
        config.setMaximumPoolSize(primaryMaxPoolSize);
        config.setMinimumIdle(primaryMinIdle);
        config.setIdleTimeout(primaryIdleTimeout);
        config.setConnectionTimeout(primaryConnectionTimeout);
        config.setMaxLifetime(primaryMaxLifetime);

        // Performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");

        return new HikariDataSource(config);
    }

    /**
     * Create replica (read) datasource with HikariCP
     * @return Replica HikariDataSource
     */
    @Bean
    public DataSource replicaDataSource() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(replicaUrl);
        config.setUsername(replicaUsername);
        config.setPassword(replicaPassword);
        config.setDriverClassName(replicaDriverClassName);
        config.setPoolName(replicaPoolName);
        config.setMaximumPoolSize(replicaMaxPoolSize);
        config.setMinimumIdle(replicaMinIdle);
        config.setIdleTimeout(replicaIdleTimeout);
        config.setConnectionTimeout(replicaConnectionTimeout);
        config.setMaxLifetime(replicaMaxLifetime);

        // Performance optimizations
        config.addDataSourceProperty("cachePrepStmts", "true");
        config.addDataSourceProperty("prepStmtCacheSize", "250");
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
        config.addDataSourceProperty("useServerPrepStmts", "true");
        config.setReadOnly(true);

        return new HikariDataSource(config);
    }

    /**
     * Create routing datasource that switches between primary and replica
     * @return Routing DataSource
     */
    @Bean
    @Primary
    public DataSource dataSource() {
        RoutingDataSource routingDataSource = new RoutingDataSource();

        Map<Object, Object> targetDataSourceMap = new HashMap<>();
        targetDataSourceMap.put(DataSourceType.PRIMARY, primaryDataSource());
        targetDataSourceMap.put(DataSourceType.REPLICA, replicaDataSource());

        routingDataSource.setTargetDataSources(targetDataSourceMap);
        routingDataSource.setDefaultTargetDataSource(primaryDataSource());

        return routingDataSource;
    }
}
