package com.portfolio.application.dto;

import java.util.Map;

/**
 * DTO for query performance comparison results
 * Contains comparison data between optimized and unoptimized queries
 */
public class QueryComparisonDto {

    private String scenarioName;
    private String description;
    private QueryResult unoptimizedQuery;
    private QueryResult optimizedQuery;
    private double improvementPercentage;
    private String recommendation;

    /**
     * Nested class for individual query results
     */
    public static class QueryResult {
        private String sql;
        private String formattedSql;
        private long executionTimeMs;
        private long rowsExamined;
        private long rowsReturned;
        private boolean usesIndex;
        private String indexUsed;
        private Map<String, Object> executionPlan;
        private String explanation;

        /**
         * Default constructor
         */
        public QueryResult() {
        }

        /**
         * Get SQL
         * @return String - SQL query
         */
        public String getSql() {
            return sql;
        }

        /**
         * Set SQL
         * @param sql - SQL query
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
         * Get execution time
         * @return long - time in ms
         */
        public long getExecutionTimeMs() {
            return executionTimeMs;
        }

        /**
         * Set execution time
         * @param executionTimeMs - time in ms
         */
        public void setExecutionTimeMs(long executionTimeMs) {
            this.executionTimeMs = executionTimeMs;
        }

        /**
         * Get rows examined
         * @return long - rows examined
         */
        public long getRowsExamined() {
            return rowsExamined;
        }

        /**
         * Set rows examined
         * @param rowsExamined - rows examined
         */
        public void setRowsExamined(long rowsExamined) {
            this.rowsExamined = rowsExamined;
        }

        /**
         * Get rows returned
         * @return long - rows returned
         */
        public long getRowsReturned() {
            return rowsReturned;
        }

        /**
         * Set rows returned
         * @param rowsReturned - rows returned
         */
        public void setRowsReturned(long rowsReturned) {
            this.rowsReturned = rowsReturned;
        }

        /**
         * Check if uses index
         * @return boolean - index usage
         */
        public boolean isUsesIndex() {
            return usesIndex;
        }

        /**
         * Set uses index
         * @param usesIndex - index usage
         */
        public void setUsesIndex(boolean usesIndex) {
            this.usesIndex = usesIndex;
        }

        /**
         * Get index used
         * @return String - index name
         */
        public String getIndexUsed() {
            return indexUsed;
        }

        /**
         * Set index used
         * @param indexUsed - index name
         */
        public void setIndexUsed(String indexUsed) {
            this.indexUsed = indexUsed;
        }

        /**
         * Get execution plan
         * @return Map - execution plan details
         */
        public Map<String, Object> getExecutionPlan() {
            return executionPlan;
        }

        /**
         * Set execution plan
         * @param executionPlan - execution plan details
         */
        public void setExecutionPlan(Map<String, Object> executionPlan) {
            this.executionPlan = executionPlan;
        }

        /**
         * Get explanation
         * @return String - explanation
         */
        public String getExplanation() {
            return explanation;
        }

        /**
         * Set explanation
         * @param explanation - explanation
         */
        public void setExplanation(String explanation) {
            this.explanation = explanation;
        }

        /**
         * Builder for QueryResult
         */
        public static class Builder {
            private final QueryResult result = new QueryResult();

            public Builder sql(String sql) {
                result.sql = sql;
                return this;
            }

            public Builder formattedSql(String formattedSql) {
                result.formattedSql = formattedSql;
                return this;
            }

            public Builder executionTimeMs(long executionTimeMs) {
                result.executionTimeMs = executionTimeMs;
                return this;
            }

            public Builder rowsExamined(long rowsExamined) {
                result.rowsExamined = rowsExamined;
                return this;
            }

            public Builder rowsReturned(long rowsReturned) {
                result.rowsReturned = rowsReturned;
                return this;
            }

            public Builder usesIndex(boolean usesIndex) {
                result.usesIndex = usesIndex;
                return this;
            }

            public Builder indexUsed(String indexUsed) {
                result.indexUsed = indexUsed;
                return this;
            }

            public Builder executionPlan(Map<String, Object> executionPlan) {
                result.executionPlan = executionPlan;
                return this;
            }

            public Builder explanation(String explanation) {
                result.explanation = explanation;
                return this;
            }

            public QueryResult build() {
                return result;
            }
        }
    }

    /**
     * Default constructor
     */
    public QueryComparisonDto() {
    }

    /**
     * Get scenario name
     * @return String - scenario name
     */
    public String getScenarioName() {
        return scenarioName;
    }

    /**
     * Set scenario name
     * @param scenarioName - scenario name
     */
    public void setScenarioName(String scenarioName) {
        this.scenarioName = scenarioName;
    }

    /**
     * Get description
     * @return String - description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description
     * @param description - description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get unoptimized query
     * @return QueryResult - unoptimized query
     */
    public QueryResult getUnoptimizedQuery() {
        return unoptimizedQuery;
    }

    /**
     * Set unoptimized query
     * @param unoptimizedQuery - unoptimized query
     */
    public void setUnoptimizedQuery(QueryResult unoptimizedQuery) {
        this.unoptimizedQuery = unoptimizedQuery;
    }

    /**
     * Get optimized query
     * @return QueryResult - optimized query
     */
    public QueryResult getOptimizedQuery() {
        return optimizedQuery;
    }

    /**
     * Set optimized query
     * @param optimizedQuery - optimized query
     */
    public void setOptimizedQuery(QueryResult optimizedQuery) {
        this.optimizedQuery = optimizedQuery;
    }

    /**
     * Get improvement percentage
     * @return double - improvement percentage
     */
    public double getImprovementPercentage() {
        return improvementPercentage;
    }

    /**
     * Set improvement percentage
     * @param improvementPercentage - improvement percentage
     */
    public void setImprovementPercentage(double improvementPercentage) {
        this.improvementPercentage = improvementPercentage;
    }

    /**
     * Get recommendation
     * @return String - recommendation
     */
    public String getRecommendation() {
        return recommendation;
    }

    /**
     * Set recommendation
     * @param recommendation - recommendation
     */
    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    /**
     * Builder for QueryComparisonDto
     */
    public static class Builder {
        private final QueryComparisonDto dto = new QueryComparisonDto();

        public Builder scenarioName(String scenarioName) {
            dto.scenarioName = scenarioName;
            return this;
        }

        public Builder description(String description) {
            dto.description = description;
            return this;
        }

        public Builder unoptimizedQuery(QueryResult unoptimizedQuery) {
            dto.unoptimizedQuery = unoptimizedQuery;
            return this;
        }

        public Builder optimizedQuery(QueryResult optimizedQuery) {
            dto.optimizedQuery = optimizedQuery;
            return this;
        }

        public Builder calculateImprovement() {
            if (dto.unoptimizedQuery != null && dto.optimizedQuery != null) {
                long unoptTime = dto.unoptimizedQuery.getExecutionTimeMs();
                long optTime = dto.optimizedQuery.getExecutionTimeMs();

                if (unoptTime > 0) {
                    dto.improvementPercentage = ((double) (unoptTime - optTime) / unoptTime) * 100;
                }
            }
            return this;
        }

        public Builder recommendation(String recommendation) {
            dto.recommendation = recommendation;
            return this;
        }

        public QueryComparisonDto build() {
            return dto;
        }
    }
}
