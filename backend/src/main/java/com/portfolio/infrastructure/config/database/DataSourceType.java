package com.portfolio.infrastructure.config.database;

/**
 * Enum for datasource types (Read/Write splitting)
 */
public enum DataSourceType {
    PRIMARY,    // Write operations
    REPLICA     // Read operations
}
