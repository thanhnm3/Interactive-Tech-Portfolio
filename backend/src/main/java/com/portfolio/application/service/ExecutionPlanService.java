package com.portfolio.application.service;

import com.portfolio.application.dto.ExecutionPlanDto;
import com.portfolio.application.dto.ExecutionPlanDto.PlanNode;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for parsing and analyzing query execution plans
 * Provides insights for database performance tuning
 */
@Service
public class ExecutionPlanService {

    /**
     * Parse simulated execution plan for demonstration
     * In production, this would use actual EXPLAIN ANALYZE results
     * @param sql - SQL query
     * @param isOptimized - whether query is optimized
     * @return ExecutionPlanDto - parsed execution plan
     */
    public ExecutionPlanDto parseExecutionPlan(String sql, boolean isOptimized) {
        ExecutionPlanDto plan = new ExecutionPlanDto();
        plan.setSql(sql);

        List<PlanNode> nodes = new ArrayList<>();
        List<String> warnings = new ArrayList<>();
        List<String> suggestions = new ArrayList<>();

        if (isOptimized) {
            plan.setPlanType("Index Scan");
            plan.setTotalCost(15.5);
            plan.setActualTime(2.3);
            plan.setRowsReturned(100);

            PlanNode indexScan = createIndexScanNode();
            nodes.add(indexScan);
        } else {
            plan.setPlanType("Sequential Scan");
            plan.setTotalCost(1250.0);
            plan.setActualTime(156.8);
            plan.setRowsReturned(100);

            PlanNode seqScan = createSeqScanNode();
            nodes.add(seqScan);

            warnings.add("Sequential scan on large table - performance may degrade with data growth");
            suggestions.add("Consider adding an index on the filtered columns");
            suggestions.add("Review query WHERE clause for indexable conditions");
        }

        plan.setNodes(nodes);
        plan.setWarnings(warnings);
        plan.setSuggestions(suggestions);

        return plan;
    }

    /**
     * Create simulated index scan node
     * @return PlanNode - index scan node
     */
    private PlanNode createIndexScanNode() {
        PlanNode node = new PlanNode();
        node.setNodeType("Index Scan");
        node.setRelationName("products");
        node.setAlias("p");
        node.setStartupCost(0.29);
        node.setTotalCost(15.5);
        node.setPlanRows(100);
        node.setPlanWidth(256);
        node.setActualStartupTime(0.05);
        node.setActualTotalTime(2.3);
        node.setActualRows(100L);
        node.setActualLoops(1L);
        node.setIndexName("idx_products_category_active");
        node.setIndexCondition("(category_id = $1 AND is_active = true)");

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("indexType", "B-tree");
        additionalInfo.put("scanDirection", "Forward");
        additionalInfo.put("heapFetches", 100);
        node.setAdditionalInfo(additionalInfo);

        return node;
    }

    /**
     * Create simulated sequential scan node
     * @return PlanNode - sequential scan node
     */
    private PlanNode createSeqScanNode() {
        PlanNode node = new PlanNode();
        node.setNodeType("Seq Scan");
        node.setRelationName("products");
        node.setAlias("p");
        node.setStartupCost(0.0);
        node.setTotalCost(1250.0);
        node.setPlanRows(100);
        node.setPlanWidth(256);
        node.setActualStartupTime(0.02);
        node.setActualTotalTime(156.8);
        node.setActualRows(100L);
        node.setActualLoops(1L);
        node.setFilter("(category_id = $1 AND is_active = true)");

        Map<String, Object> additionalInfo = new HashMap<>();
        additionalInfo.put("rowsRemovedByFilter", 9900);
        additionalInfo.put("tableSize", "10000 rows");
        node.setAdditionalInfo(additionalInfo);

        return node;
    }

    /**
     * Analyze execution plan and provide recommendations
     * @param plan - execution plan to analyze
     * @return Map - analysis results
     */
    public Map<String, Object> analyzePlan(ExecutionPlanDto plan) {
        Map<String, Object> analysis = new HashMap<>();

        List<String> issues = new ArrayList<>();
        List<String> recommendations = new ArrayList<>();
        int performanceScore = 100;

        for (PlanNode node : plan.getNodes()) {
            analyzeNode(node, issues, recommendations, performanceScore);
        }

        // Calculate performance score based on issues
        performanceScore = Math.max(0, performanceScore - (issues.size() * 15));

        analysis.put("performanceScore", performanceScore);
        analysis.put("issues", issues);
        analysis.put("recommendations", recommendations);
        analysis.put("summary", generateSummary(plan, performanceScore));

        return analysis;
    }

    /**
     * Analyze individual plan node
     * @param node - plan node
     * @param issues - list to add issues
     * @param recommendations - list to add recommendations
     * @param score - current score
     */
    private void analyzeNode(PlanNode node, List<String> issues,
                            List<String> recommendations, int score) {

        // Check for sequential scans
        if ("Seq Scan".equals(node.getNodeType())) {
            issues.add(String.format("Sequential scan on table '%s' - examines all rows", node.getRelationName()));
            recommendations.add(String.format("Create index on '%s' for filtered columns", node.getRelationName()));
        }

        // Check for high cost operations
        if (node.getTotalCost() > 1000) {
            issues.add(String.format("High cost operation (%.2f) on '%s'", node.getTotalCost(), node.getRelationName()));
        }

        // Check for large row estimation vs actual
        if (node.getActualRows() != null && node.getPlanRows() > 0) {
            double ratio = (double) node.getActualRows() / node.getPlanRows();
            if (ratio > 10 || ratio < 0.1) {
                issues.add("Row estimation significantly different from actual - statistics may be stale");
                recommendations.add("Run ANALYZE on the table to update statistics");
            }
        }

        // Check for filter operations after scan
        if (node.getFilter() != null && !node.getFilter().isEmpty()) {
            Map<String, Object> info = node.getAdditionalInfo();
            if (info != null && info.containsKey("rowsRemovedByFilter")) {
                long removed = ((Number) info.get("rowsRemovedByFilter")).longValue();
                if (removed > 1000) {
                    issues.add(String.format("Filter removed %d rows - index might help", removed));
                }
            }
        }

        // Recursively analyze children
        if (node.getChildren() != null) {
            for (PlanNode child : node.getChildren()) {
                analyzeNode(child, issues, recommendations, score);
            }
        }
    }

    /**
     * Generate summary based on analysis
     * @param plan - execution plan
     * @param score - performance score
     * @return String - summary text
     */
    private String generateSummary(ExecutionPlanDto plan, int score) {
        String level;

        if (score >= 80) {
            level = "Good";
        } else if (score >= 60) {
            level = "Needs Improvement";
        } else if (score >= 40) {
            level = "Poor";
        } else {
            level = "Critical";
        }

        return String.format("Performance Level: %s (Score: %d/100). " +
            "Total Cost: %.2f, Execution Time: %.2f ms, Rows: %d",
            level, score, plan.getTotalCost(), plan.getActualTime(), plan.getRowsReturned());
    }

    /**
     * Compare two execution plans
     * @param plan1 - first plan
     * @param plan2 - second plan
     * @return Map - comparison results
     */
    public Map<String, Object> comparePlans(ExecutionPlanDto plan1, ExecutionPlanDto plan2) {
        Map<String, Object> comparison = new HashMap<>();

        double costImprovement = ((plan1.getTotalCost() - plan2.getTotalCost()) / plan1.getTotalCost()) * 100;
        double timeImprovement = ((plan1.getActualTime() - plan2.getActualTime()) / plan1.getActualTime()) * 100;

        comparison.put("costImprovement", String.format("%.2f%%", costImprovement));
        comparison.put("timeImprovement", String.format("%.2f%%", timeImprovement));
        comparison.put("plan1Type", plan1.getPlanType());
        comparison.put("plan2Type", plan2.getPlanType());
        comparison.put("isImproved", costImprovement > 0 && timeImprovement > 0);

        String verdict;

        if (costImprovement > 50 && timeImprovement > 50) {
            verdict = "Significant improvement achieved through optimization";
        } else if (costImprovement > 0 && timeImprovement > 0) {
            verdict = "Moderate improvement achieved";
        } else {
            verdict = "No significant improvement - consider alternative optimizations";
        }

        comparison.put("verdict", verdict);

        return comparison;
    }
}
