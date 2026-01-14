package com.portfolio.infrastructure.config.database;

import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

/**
 * Routing datasource that switches between primary and replica
 */
public class RoutingDataSource extends AbstractRoutingDataSource {

    /**
     * Determine current lookup key based on context
     * @return Current datasource type
     */
    @Override
    protected Object determineCurrentLookupKey() {
        return DataSourceContextHolder.getDataSourceType();
    }
}
