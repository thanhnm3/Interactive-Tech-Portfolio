package com.portfolio;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.data.redis.RedisAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Main application class for Portfolio Backend
 * Demonstrates Clean Architecture with Spring Boot
 */
@SpringBootApplication(exclude = {RedisAutoConfiguration.class})
@EnableCaching
@EnableAsync
@EnableTransactionManagement
public class PortfolioApplication {

    /**
     * Application entry point
     * @param args - command line arguments
     */
    public static void main(String[] args) {
        SpringApplication.run(PortfolioApplication.class, args);
    }
}
