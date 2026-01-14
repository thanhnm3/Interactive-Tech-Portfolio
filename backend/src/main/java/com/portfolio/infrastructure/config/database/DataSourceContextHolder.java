package com.portfolio.infrastructure.config.database;

/**
 * Thread-local holder for current datasource context
 */
public final class DataSourceContextHolder {

    private static final ThreadLocal<DataSourceType> CONTEXT_HOLDER = new ThreadLocal<>();

    private DataSourceContextHolder() {
        // Prevent instantiation
    }

    /**
     * Set current datasource type
     * @param dataSourceType Datasource type to set
     */
    public static void setDataSourceType(DataSourceType dataSourceType) {
        CONTEXT_HOLDER.set(dataSourceType);
    }

    /**
     * Get current datasource type
     * @return Current datasource type, defaults to PRIMARY
     */
    public static DataSourceType getDataSourceType() {
        DataSourceType type = CONTEXT_HOLDER.get();
        return type != null ? type : DataSourceType.PRIMARY;
    }

    /**
     * Clear current datasource context
     */
    public static void clearDataSourceType() {
        CONTEXT_HOLDER.remove();
    }

    /**
     * Set datasource to primary (write)
     */
    public static void usePrimary() {
        setDataSourceType(DataSourceType.PRIMARY);
    }

    /**
     * Set datasource to replica (read)
     */
    public static void useReplica() {
        setDataSourceType(DataSourceType.REPLICA);
    }
}
