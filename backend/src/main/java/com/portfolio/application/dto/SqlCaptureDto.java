package com.portfolio.application.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * DTO for captured SQL query information
 * Contains the SQL, parameters, and execution metadata
 */
public class SqlCaptureDto {

    private String sql;
    private String formattedSql;
    private List<Object> parameters;
    private Map<String, Object> namedParameters;
    private String operationType;
    private String entityName;
    private long executionTimeMs;
    private LocalDateTime capturedAt;
    private String jpaMethod;

    /**
     * Default constructor
     */
    public SqlCaptureDto() {
        this.capturedAt = LocalDateTime.now();
    }

    /**
     * Create SQL capture with all properties
     * @param sql - raw SQL
     * @param formattedSql - formatted SQL
     * @param parameters - positional parameters
     * @param namedParameters - named parameters
     * @param operationType - SELECT, INSERT, etc.
     * @param entityName - entity involved
     * @param executionTimeMs - execution time
     * @param jpaMethod - JPA method that generated the SQL
     */
    public SqlCaptureDto(String sql, String formattedSql, List<Object> parameters,
                         Map<String, Object> namedParameters, String operationType,
                         String entityName, long executionTimeMs, String jpaMethod) {
        this.sql = sql;
        this.formattedSql = formattedSql;
        this.parameters = parameters;
        this.namedParameters = namedParameters;
        this.operationType = operationType;
        this.entityName = entityName;
        this.executionTimeMs = executionTimeMs;
        this.jpaMethod = jpaMethod;
        this.capturedAt = LocalDateTime.now();
    }

    /**
     * Get raw SQL
     * @return String - SQL
     */
    public String getSql() {
        return sql;
    }

    /**
     * Set raw SQL
     * @param sql - SQL
     */
    public void setSql(String sql) {
        this.sql = sql;
    }

    /**
     * Get formatted SQL
     * @return String - formatted SQL
     */
    public String getFormattedSql() {
        return formattedSql;
    }

    /**
     * Set formatted SQL
     * @param formattedSql - formatted SQL
     */
    public void setFormattedSql(String formattedSql) {
        this.formattedSql = formattedSql;
    }

    /**
     * Get parameters
     * @return List - positional parameters
     */
    public List<Object> getParameters() {
        return parameters;
    }

    /**
     * Set parameters
     * @param parameters - positional parameters
     */
    public void setParameters(List<Object> parameters) {
        this.parameters = parameters;
    }

    /**
     * Get named parameters
     * @return Map - named parameters
     */
    public Map<String, Object> getNamedParameters() {
        return namedParameters;
    }

    /**
     * Set named parameters
     * @param namedParameters - named parameters
     */
    public void setNamedParameters(Map<String, Object> namedParameters) {
        this.namedParameters = namedParameters;
    }

    /**
     * Get operation type
     * @return String - operation type
     */
    public String getOperationType() {
        return operationType;
    }

    /**
     * Set operation type
     * @param operationType - operation type
     */
    public void setOperationType(String operationType) {
        this.operationType = operationType;
    }

    /**
     * Get entity name
     * @return String - entity name
     */
    public String getEntityName() {
        return entityName;
    }

    /**
     * Set entity name
     * @param entityName - entity name
     */
    public void setEntityName(String entityName) {
        this.entityName = entityName;
    }

    /**
     * Get execution time
     * @return long - execution time in ms
     */
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    /**
     * Set execution time
     * @param executionTimeMs - execution time in ms
     */
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    /**
     * Get capture timestamp
     * @return LocalDateTime - captured at
     */
    public LocalDateTime getCapturedAt() {
        return capturedAt;
    }

    /**
     * Set capture timestamp
     * @param capturedAt - captured at
     */
    public void setCapturedAt(LocalDateTime capturedAt) {
        this.capturedAt = capturedAt;
    }

    /**
     * Get JPA method
     * @return String - JPA method name
     */
    public String getJpaMethod() {
        return jpaMethod;
    }

    /**
     * Set JPA method
     * @param jpaMethod - JPA method name
     */
    public void setJpaMethod(String jpaMethod) {
        this.jpaMethod = jpaMethod;
    }

    /**
     * Builder for SqlCaptureDto
     */
    public static class Builder {
        private String sql;
        private String formattedSql;
        private List<Object> parameters;
        private Map<String, Object> namedParameters;
        private String operationType;
        private String entityName;
        private long executionTimeMs;
        private String jpaMethod;

        public Builder sql(String sql) {
            this.sql = sql;
            return this;
        }

        public Builder formattedSql(String formattedSql) {
            this.formattedSql = formattedSql;
            return this;
        }

        public Builder parameters(List<Object> parameters) {
            this.parameters = parameters;
            return this;
        }

        public Builder namedParameters(Map<String, Object> namedParameters) {
            this.namedParameters = namedParameters;
            return this;
        }

        public Builder operationType(String operationType) {
            this.operationType = operationType;
            return this;
        }

        public Builder entityName(String entityName) {
            this.entityName = entityName;
            return this;
        }

        public Builder executionTimeMs(long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
            return this;
        }

        public Builder jpaMethod(String jpaMethod) {
            this.jpaMethod = jpaMethod;
            return this;
        }

        public SqlCaptureDto build() {
            return new SqlCaptureDto(sql, formattedSql, parameters, namedParameters,
                operationType, entityName, executionTimeMs, jpaMethod);
        }
    }
}
