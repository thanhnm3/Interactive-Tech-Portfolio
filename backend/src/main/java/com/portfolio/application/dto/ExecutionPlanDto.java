package com.portfolio.application.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO for database query execution plan
 * Contains parsed EXPLAIN ANALYZE results
 */
public class ExecutionPlanDto {

    private String sql;
    private String planType;
    private List<PlanNode> nodes;
    private double totalCost;
    private double actualTime;
    private long rowsReturned;
    private List<String> warnings;
    private List<String> suggestions;

    /**
     * Represents a node in the execution plan tree
     */
    public static class PlanNode {
        private String nodeType;
        private String relationName;
        private String alias;
        private double startupCost;
        private double totalCost;
        private long planRows;
        private int planWidth;
        private Double actualStartupTime;
        private Double actualTotalTime;
        private Long actualRows;
        private Long actualLoops;
        private String indexName;
        private String indexCondition;
        private String filter;
        private List<PlanNode> children;
        private Map<String, Object> additionalInfo;

        /**
         * Default constructor
         */
        public PlanNode() {
        }

        /**
         * Get node type
         * @return String - node type (Seq Scan, Index Scan, etc.)
         */
        public String getNodeType() {
            return nodeType;
        }

        /**
         * Set node type
         * @param nodeType - node type
         */
        public void setNodeType(String nodeType) {
            this.nodeType = nodeType;
        }

        /**
         * Get relation name
         * @return String - table name
         */
        public String getRelationName() {
            return relationName;
        }

        /**
         * Set relation name
         * @param relationName - table name
         */
        public void setRelationName(String relationName) {
            this.relationName = relationName;
        }

        /**
         * Get alias
         * @return String - table alias
         */
        public String getAlias() {
            return alias;
        }

        /**
         * Set alias
         * @param alias - table alias
         */
        public void setAlias(String alias) {
            this.alias = alias;
        }

        /**
         * Get startup cost
         * @return double - startup cost
         */
        public double getStartupCost() {
            return startupCost;
        }

        /**
         * Set startup cost
         * @param startupCost - startup cost
         */
        public void setStartupCost(double startupCost) {
            this.startupCost = startupCost;
        }

        /**
         * Get total cost
         * @return double - total cost
         */
        public double getTotalCost() {
            return totalCost;
        }

        /**
         * Set total cost
         * @param totalCost - total cost
         */
        public void setTotalCost(double totalCost) {
            this.totalCost = totalCost;
        }

        /**
         * Get plan rows
         * @return long - estimated rows
         */
        public long getPlanRows() {
            return planRows;
        }

        /**
         * Set plan rows
         * @param planRows - estimated rows
         */
        public void setPlanRows(long planRows) {
            this.planRows = planRows;
        }

        /**
         * Get plan width
         * @return int - row width
         */
        public int getPlanWidth() {
            return planWidth;
        }

        /**
         * Set plan width
         * @param planWidth - row width
         */
        public void setPlanWidth(int planWidth) {
            this.planWidth = planWidth;
        }

        /**
         * Get actual startup time
         * @return Double - actual startup time
         */
        public Double getActualStartupTime() {
            return actualStartupTime;
        }

        /**
         * Set actual startup time
         * @param actualStartupTime - actual startup time
         */
        public void setActualStartupTime(Double actualStartupTime) {
            this.actualStartupTime = actualStartupTime;
        }

        /**
         * Get actual total time
         * @return Double - actual total time
         */
        public Double getActualTotalTime() {
            return actualTotalTime;
        }

        /**
         * Set actual total time
         * @param actualTotalTime - actual total time
         */
        public void setActualTotalTime(Double actualTotalTime) {
            this.actualTotalTime = actualTotalTime;
        }

        /**
         * Get actual rows
         * @return Long - actual rows returned
         */
        public Long getActualRows() {
            return actualRows;
        }

        /**
         * Set actual rows
         * @param actualRows - actual rows returned
         */
        public void setActualRows(Long actualRows) {
            this.actualRows = actualRows;
        }

        /**
         * Get actual loops
         * @return Long - number of loops
         */
        public Long getActualLoops() {
            return actualLoops;
        }

        /**
         * Set actual loops
         * @param actualLoops - number of loops
         */
        public void setActualLoops(Long actualLoops) {
            this.actualLoops = actualLoops;
        }

        /**
         * Get index name
         * @return String - index used
         */
        public String getIndexName() {
            return indexName;
        }

        /**
         * Set index name
         * @param indexName - index used
         */
        public void setIndexName(String indexName) {
            this.indexName = indexName;
        }

        /**
         * Get index condition
         * @return String - index condition
         */
        public String getIndexCondition() {
            return indexCondition;
        }

        /**
         * Set index condition
         * @param indexCondition - index condition
         */
        public void setIndexCondition(String indexCondition) {
            this.indexCondition = indexCondition;
        }

        /**
         * Get filter
         * @return String - filter condition
         */
        public String getFilter() {
            return filter;
        }

        /**
         * Set filter
         * @param filter - filter condition
         */
        public void setFilter(String filter) {
            this.filter = filter;
        }

        /**
         * Get children
         * @return List - child nodes
         */
        public List<PlanNode> getChildren() {
            return children;
        }

        /**
         * Set children
         * @param children - child nodes
         */
        public void setChildren(List<PlanNode> children) {
            this.children = children;
        }

        /**
         * Get additional info
         * @return Map - additional information
         */
        public Map<String, Object> getAdditionalInfo() {
            return additionalInfo;
        }

        /**
         * Set additional info
         * @param additionalInfo - additional information
         */
        public void setAdditionalInfo(Map<String, Object> additionalInfo) {
            this.additionalInfo = additionalInfo;
        }
    }

    /**
     * Default constructor
     */
    public ExecutionPlanDto() {
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
     * Get plan type
     * @return String - plan type
     */
    public String getPlanType() {
        return planType;
    }

    /**
     * Set plan type
     * @param planType - plan type
     */
    public void setPlanType(String planType) {
        this.planType = planType;
    }

    /**
     * Get nodes
     * @return List - plan nodes
     */
    public List<PlanNode> getNodes() {
        return nodes;
    }

    /**
     * Set nodes
     * @param nodes - plan nodes
     */
    public void setNodes(List<PlanNode> nodes) {
        this.nodes = nodes;
    }

    /**
     * Get total cost
     * @return double - total cost
     */
    public double getTotalCost() {
        return totalCost;
    }

    /**
     * Set total cost
     * @param totalCost - total cost
     */
    public void setTotalCost(double totalCost) {
        this.totalCost = totalCost;
    }

    /**
     * Get actual time
     * @return double - actual execution time
     */
    public double getActualTime() {
        return actualTime;
    }

    /**
     * Set actual time
     * @param actualTime - actual execution time
     */
    public void setActualTime(double actualTime) {
        this.actualTime = actualTime;
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
     * Get warnings
     * @return List - warnings
     */
    public List<String> getWarnings() {
        return warnings;
    }

    /**
     * Set warnings
     * @param warnings - warnings
     */
    public void setWarnings(List<String> warnings) {
        this.warnings = warnings;
    }

    /**
     * Get suggestions
     * @return List - optimization suggestions
     */
    public List<String> getSuggestions() {
        return suggestions;
    }

    /**
     * Set suggestions
     * @param suggestions - optimization suggestions
     */
    public void setSuggestions(List<String> suggestions) {
        this.suggestions = suggestions;
    }
}
