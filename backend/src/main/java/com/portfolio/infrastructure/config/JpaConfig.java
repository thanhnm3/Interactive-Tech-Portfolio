package com.portfolio.infrastructure.config;

import com.portfolio.infrastructure.persistence.HibernateQueryInterceptor;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * JPA and Hibernate configuration
 */
@Configuration
public class JpaConfig {

    private final HibernateQueryInterceptor queryInterceptor;

    /**
     * Constructor with dependency injection
     * @param queryInterceptor - query interceptor
     */
    public JpaConfig(HibernateQueryInterceptor queryInterceptor) {
        this.queryInterceptor = queryInterceptor;
    }

    /**
     * Customize Hibernate properties to add statement inspector
     * @return HibernatePropertiesCustomizer - customizer bean
     */
    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer() {
        return hibernateProperties -> hibernateProperties.put(
            "hibernate.session_factory.statement_inspector",
            queryInterceptor
        );
    }
}
