package com.portfolio.config;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import java.time.Duration;

/**
 * Testcontainers configuration for integration tests
 * Provides PostgreSQL container for database tests
 * 
 * Usage: Extend this class in test classes that need database access
 */
@Testcontainers
public abstract class TestContainersConfiguration {

    @Container
    static final PostgreSQLContainer<?> postgresContainer = new PostgreSQLContainer<>(
        DockerImageName.parse("postgres:15-alpine"))
        .withDatabaseName("portfolio_test")
        .withUsername("test")
        .withPassword("test")
        .withReuse(true)
        .waitingFor(Wait.forListeningPort()
            .withStartupTimeout(Duration.ofSeconds(120)))
        .withStartupTimeout(Duration.ofSeconds(120));

    /**
     * Configure dynamic properties for datasource
     * This method will be called by Spring Test Context
     * @param registry - property registry
     */
    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresContainer::getUsername);
        registry.add("spring.datasource.password", postgresContainer::getPassword);
    }
}
