package com.portfolio.application.service;

import com.portfolio.application.dto.ExecutionPlanDto;
import com.portfolio.application.dto.QueryComparisonDto;
import com.portfolio.application.dto.QueryComparisonDto.QueryResult;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for query performance comparison and optimization demonstration
 * Provides scenarios comparing optimized vs unoptimized queries
 */
@Service
public class QueryPerformanceService {

    private final ExecutionPlanService executionPlanService;
    private final Map<String, QueryScenario> scenarios;

    /**
     * Query scenario definition
     */
    private record QueryScenario(
        String name,
        String description,
        String unoptimizedSql,
        String optimizedSql,
        String optimization,
        String recommendation
    ) {}

    /**
     * Constructor with dependency injection
     * @param executionPlanService - execution plan service
     */
    public QueryPerformanceService(ExecutionPlanService executionPlanService) {
        this.executionPlanService = executionPlanService;
        this.scenarios = initializeScenarios();
    }

    /**
     * Initialize query comparison scenarios
     * @return Map - scenarios by key
     */
    private Map<String, QueryScenario> initializeScenarios() {
        Map<String, QueryScenario> scenarios = new HashMap<>();

        // Scenario 1: Index usage
        scenarios.put("index-usage", new QueryScenario(
            "Index vs Full Table Scan",
            "Demonstrates the performance difference between using an index and performing a full table scan",
            """
                -- Unoptimized: Full table scan
                SELECT *
                FROM products p
                WHERE p.category_id = '550e8400-e29b-41d4-a716-446655440000'
                  AND p.is_active = true
                ORDER BY p.created_at DESC;
                
                -- No index on (category_id, is_active)
                -- Scans all 10,000 rows
                """,
            """
                -- Optimized: Uses composite index
                SELECT p.id, p.name, p.price, p.stock_quantity
                FROM products p
                WHERE p.category_id = '550e8400-e29b-41d4-a716-446655440000'
                  AND p.is_active = true
                ORDER BY p.created_at DESC;
                
                -- CREATE INDEX idx_products_category_active
                -- ON products(category_id, is_active, created_at DESC);
                """,
            "Added composite index on (category_id, is_active, created_at) and selected only needed columns",
            "Create composite indexes for frequently used WHERE clause combinations"
        ));

        // Scenario 2: N+1 Query Problem
        scenarios.put("n-plus-one", new QueryScenario(
            "N+1 Query Problem",
            "Shows the impact of N+1 queries and how to fix with JOIN or batch loading",
            """
                -- Unoptimized: N+1 queries
                -- First query: Get all orders
                SELECT * FROM orders WHERE user_id = ?;
                
                -- Then for EACH order (N times):
                SELECT * FROM order_items WHERE order_id = ?;
                SELECT * FROM products WHERE id = ?;
                
                -- If 100 orders: 1 + 100 + 100 = 201 queries!
                """,
            """
                -- Optimized: Single query with JOIN
                SELECT o.id, o.order_number, o.total_amount,
                       oi.quantity, oi.unit_price,
                       p.name as product_name, p.sku
                FROM orders o
                JOIN order_items oi ON o.id = oi.order_id
                JOIN products p ON oi.product_id = p.id
                WHERE o.user_id = ?
                ORDER BY o.created_at DESC;
                
                -- Single query returns all data!
                """,
            "Replaced N+1 queries with a single JOIN query",
            "Use JPA fetch joins or batch fetching to avoid N+1 queries"
        ));

        // Scenario 3: Pagination
        scenarios.put("pagination", new QueryScenario(
            "Efficient Pagination",
            "Compares OFFSET-based pagination vs keyset pagination for large datasets",
            """
                -- Unoptimized: OFFSET pagination
                SELECT *
                FROM products
                ORDER BY created_at DESC
                OFFSET 10000 LIMIT 20;
                
                -- Database must scan and discard 10,000 rows!
                -- Gets slower as page number increases
                """,
            """
                -- Optimized: Keyset pagination
                SELECT *
                FROM products
                WHERE created_at < '2024-01-15T10:30:00'
                  AND id < '550e8400-e29b-41d4-a716-446655440000'
                ORDER BY created_at DESC, id DESC
                LIMIT 20;
                
                -- Uses index seek, constant performance
                -- Pass last row's values as cursor
                """,
            "Replaced OFFSET with keyset/cursor pagination",
            "Use keyset pagination for large datasets to maintain consistent performance"
        ));

        // Scenario 4: Aggregate optimization
        scenarios.put("aggregate", new QueryScenario(
            "Aggregate Query Optimization",
            "Shows how to optimize aggregate queries with proper indexing and materialized views",
            """
                -- Unoptimized: Real-time aggregation
                SELECT c.name as category,
                       COUNT(*) as product_count,
                       AVG(p.price) as avg_price,
                       SUM(p.stock_quantity) as total_stock
                FROM products p
                JOIN categories c ON p.category_id = c.id
                WHERE p.is_active = true
                GROUP BY c.id, c.name;
                
                -- Scans entire products table every time
                """,
            """
                -- Optimized: Pre-computed summary table
                SELECT category_name,
                       product_count,
                       avg_price,
                       total_stock
                FROM category_summary
                WHERE is_active = true;
                
                -- Summary table updated by triggers or scheduled job
                -- CREATE TABLE category_summary AS
                -- SELECT ... GROUP BY ... (with indexes)
                """,
            "Pre-computed aggregates in summary table updated incrementally",
            "Use materialized views or summary tables for frequently-accessed aggregates"
        ));

        // Scenario 5: Subquery vs JOIN
        scenarios.put("subquery-vs-join", new QueryScenario(
            "Subquery vs JOIN",
            "Demonstrates when JOIN outperforms correlated subqueries",
            """
                -- Unoptimized: Correlated subquery
                SELECT p.name, p.price,
                    (SELECT COUNT(*)
                     FROM order_items oi
                     WHERE oi.product_id = p.id) as times_ordered
                FROM products p
                WHERE p.is_active = true;
                
                -- Subquery executes for EACH product row
                """,
            """
                -- Optimized: LEFT JOIN with aggregation
                SELECT p.name, p.price,
                       COALESCE(order_counts.times_ordered, 0) as times_ordered
                FROM products p
                LEFT JOIN (
                    SELECT product_id, COUNT(*) as times_ordered
                    FROM order_items
                    GROUP BY product_id
                ) order_counts ON p.id = order_counts.product_id
                WHERE p.is_active = true;
                
                -- Single aggregation, single join
                """,
            "Replaced correlated subquery with derived table JOIN",
            "Convert correlated subqueries to JOINs when possible for better performance"
        ));

        return scenarios;
    }

    /**
     * Get query comparison for a scenario
     * @param scenarioKey - scenario key
     * @return QueryComparisonDto - comparison results
     */
    public QueryComparisonDto getQueryComparison(String scenarioKey) {
        QueryScenario scenario = scenarios.get(scenarioKey);

        if (scenario == null) {
            return null;
        }

        // Simulate execution for unoptimized query
        ExecutionPlanDto unoptPlan = executionPlanService.parseExecutionPlan(scenario.unoptimizedSql(), false);
        
        // Handle null values for Map.of() - Map.of() doesn't accept null values
        String unoptPlanType = unoptPlan.getPlanType() != null ? unoptPlan.getPlanType() : "Unknown";
        List<ExecutionPlanDto.PlanNode> unoptNodes = unoptPlan.getNodes() != null ? unoptPlan.getNodes() : List.of();
        
        QueryResult unoptResult = new QueryResult.Builder()
            .sql(scenario.unoptimizedSql())
            .formattedSql(scenario.unoptimizedSql())
            .executionTimeMs((long) (unoptPlan.getActualTime()))
            .rowsExamined(10000)
            .rowsReturned(unoptPlan.getRowsReturned())
            .usesIndex(false)
            .executionPlan(Map.of(
                "type", unoptPlanType,
                "cost", unoptPlan.getTotalCost(),
                "nodes", unoptNodes
            ))
            .explanation("Full table scan required - no suitable index found")
            .build();

        // Simulate execution for optimized query
        ExecutionPlanDto optPlan = executionPlanService.parseExecutionPlan(scenario.optimizedSql(), true);
        
        // Handle null values for Map.of() - Map.of() doesn't accept null values
        String optPlanType = optPlan.getPlanType() != null ? optPlan.getPlanType() : "Unknown";
        List<ExecutionPlanDto.PlanNode> optNodes = optPlan.getNodes() != null ? optPlan.getNodes() : List.of();
        
        QueryResult optResult = new QueryResult.Builder()
            .sql(scenario.optimizedSql())
            .formattedSql(scenario.optimizedSql())
            .executionTimeMs((long) (optPlan.getActualTime()))
            .rowsExamined(100)
            .rowsReturned(optPlan.getRowsReturned())
            .usesIndex(true)
            .indexUsed("idx_products_category_active")
            .executionPlan(Map.of(
                "type", optPlanType,
                "cost", optPlan.getTotalCost(),
                "nodes", optNodes
            ))
            .explanation("Index scan used - efficient row lookup")
            .build();

        return new QueryComparisonDto.Builder()
            .scenarioName(scenario.name())
            .description(scenario.description())
            .unoptimizedQuery(unoptResult)
            .optimizedQuery(optResult)
            .calculateImprovement()
            .recommendation(scenario.recommendation())
            .build();
    }

    /**
     * Get all available scenarios
     * @return List - scenario summaries
     */
    public List<Map<String, String>> getAvailableScenarios() {
        List<Map<String, String>> summaries = new ArrayList<>();

        for (Map.Entry<String, QueryScenario> entry : scenarios.entrySet()) {
            Map<String, String> summary = new HashMap<>();
            summary.put("key", entry.getKey());
            summary.put("name", entry.getValue().name());
            summary.put("description", entry.getValue().description());
            summaries.add(summary);
        }

        return summaries;
    }

    /**
     * Get detailed scenario information
     * @param scenarioKey - scenario key
     * @return Map - scenario details
     */
    public Map<String, Object> getScenarioDetails(String scenarioKey) {
        QueryScenario scenario = scenarios.get(scenarioKey);

        if (scenario == null) {
            return null;
        }

        Map<String, Object> details = new HashMap<>();
        details.put("key", scenarioKey);
        details.put("name", scenario.name());
        details.put("description", scenario.description());
        details.put("unoptimizedSql", scenario.unoptimizedSql());
        details.put("optimizedSql", scenario.optimizedSql());
        details.put("optimization", scenario.optimization());
        details.put("recommendation", scenario.recommendation());

        return details;
    }

    /**
     * Get execution plan analysis for a scenario
     * @param scenarioKey - scenario key
     * @param isOptimized - whether to get optimized plan
     * @return Map - execution plan analysis
     */
    public Map<String, Object> getExecutionPlanAnalysis(String scenarioKey, boolean isOptimized) {
        QueryScenario scenario = scenarios.get(scenarioKey);

        if (scenario == null) {
            return null;
        }

        String sql = isOptimized ? scenario.optimizedSql() : scenario.unoptimizedSql();
        ExecutionPlanDto plan = executionPlanService.parseExecutionPlan(sql, isOptimized);

        Map<String, Object> result = new HashMap<>();
        result.put("plan", plan);
        result.put("analysis", executionPlanService.analyzePlan(plan));

        return result;
    }

    /**
     * Compare execution plans for a scenario
     * @param scenarioKey - scenario key
     * @return Map - plan comparison
     */
    public Map<String, Object> comparePlans(String scenarioKey) {
        QueryScenario scenario = scenarios.get(scenarioKey);

        if (scenario == null) {
            return null;
        }

        ExecutionPlanDto unoptPlan = executionPlanService.parseExecutionPlan(scenario.unoptimizedSql(), false);
        ExecutionPlanDto optPlan = executionPlanService.parseExecutionPlan(scenario.optimizedSql(), true);

        return executionPlanService.comparePlans(unoptPlan, optPlan);
    }
}
