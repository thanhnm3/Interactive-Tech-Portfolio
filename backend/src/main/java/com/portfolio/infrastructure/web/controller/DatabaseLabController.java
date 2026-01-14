package com.portfolio.infrastructure.web.controller;

import com.portfolio.application.dto.QueryComparisonDto;
import com.portfolio.application.service.QueryPerformanceService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for database performance lab
 * Provides APIs for query optimization demonstrations
 */
@RestController
@RequestMapping("/api/v1/db-lab")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}")
public class DatabaseLabController {

    private final QueryPerformanceService queryPerformanceService;

    /**
     * Constructor with dependency injection
     * @param queryPerformanceService - query performance service
     */
    public DatabaseLabController(QueryPerformanceService queryPerformanceService) {
        this.queryPerformanceService = queryPerformanceService;
    }

    /**
     * Get available optimization scenarios
     * @return ResponseEntity - list of scenarios
     */
    @GetMapping("/scenarios")
    public ResponseEntity<List<Map<String, String>>> getAvailableScenarios() {
        List<Map<String, String>> scenarios = queryPerformanceService.getAvailableScenarios();
        return ResponseEntity.ok(scenarios);
    }

    /**
     * Get scenario details
     * @param key - scenario key
     * @return ResponseEntity - scenario details
     */
    @GetMapping("/scenarios/{key}")
    public ResponseEntity<Map<String, Object>> getScenarioDetails(@PathVariable String key) {
        Map<String, Object> details = queryPerformanceService.getScenarioDetails(key);

        if (details == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(details);
    }

    /**
     * Get query comparison for a scenario
     * @param key - scenario key
     * @return ResponseEntity - comparison results
     */
    @GetMapping("/scenarios/{key}/compare")
    public ResponseEntity<QueryComparisonDto> getQueryComparison(@PathVariable String key) {
        QueryComparisonDto comparison = queryPerformanceService.getQueryComparison(key);

        if (comparison == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(comparison);
    }

    /**
     * Get execution plan analysis for a scenario
     * @param key - scenario key
     * @param optimized - whether to get optimized plan
     * @return ResponseEntity - execution plan analysis
     */
    @GetMapping("/scenarios/{key}/plan")
    public ResponseEntity<Map<String, Object>> getExecutionPlan(
            @PathVariable String key,
            @RequestParam(defaultValue = "false") boolean optimized) {

        Map<String, Object> analysis = queryPerformanceService.getExecutionPlanAnalysis(key, optimized);

        if (analysis == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(analysis);
    }

    /**
     * Compare execution plans for a scenario
     * @param key - scenario key
     * @return ResponseEntity - plan comparison
     */
    @GetMapping("/scenarios/{key}/plan-comparison")
    public ResponseEntity<Map<String, Object>> comparePlans(@PathVariable String key) {
        Map<String, Object> comparison = queryPerformanceService.comparePlans(key);

        if (comparison == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(comparison);
    }

    /**
     * Get all comparisons for showcase
     * @return ResponseEntity - all comparisons
     */
    @GetMapping("/showcase")
    public ResponseEntity<Map<String, Object>> getShowcase() {
        List<Map<String, String>> scenarios = queryPerformanceService.getAvailableScenarios();

        Map<String, Object> showcase = Map.of(
            "title", "Database Performance Tuning Lab",
            "description", "Interactive demonstrations of SQL optimization techniques",
            "scenarios", scenarios,
            "topics", List.of(
                "Index optimization",
                "N+1 query problem",
                "Pagination strategies",
                "Aggregate optimization",
                "Query rewriting"
            )
        );

        return ResponseEntity.ok(showcase);
    }

    /**
     * Get optimization tips
     * @return ResponseEntity - optimization tips
     */
    @GetMapping("/tips")
    public ResponseEntity<List<Map<String, String>>> getOptimizationTips() {
        List<Map<String, String>> tips = List.of(
            Map.of(
                "title", "Use EXPLAIN ANALYZE",
                "description", "Always analyze query plans before and after optimization",
                "example", "EXPLAIN ANALYZE SELECT * FROM products WHERE category_id = ?"
            ),
            Map.of(
                "title", "Create Composite Indexes",
                "description", "Index columns used together in WHERE clauses",
                "example", "CREATE INDEX idx_cat_active ON products(category_id, is_active)"
            ),
            Map.of(
                "title", "Avoid SELECT *",
                "description", "Select only needed columns to reduce I/O",
                "example", "SELECT id, name, price FROM products (not SELECT *)"
            ),
            Map.of(
                "title", "Use Keyset Pagination",
                "description", "Replace OFFSET with WHERE clause for large datasets",
                "example", "WHERE created_at < :cursor ORDER BY created_at DESC LIMIT 20"
            ),
            Map.of(
                "title", "Batch Operations",
                "description", "Use batch inserts/updates instead of single-row operations",
                "example", "INSERT INTO ... VALUES (...), (...), (...)"
            ),
            Map.of(
                "title", "Optimize JOINs",
                "description", "Ensure join columns are indexed and use appropriate join types",
                "example", "Indexed foreign keys and prefer INNER JOIN when possible"
            )
        );

        return ResponseEntity.ok(tips);
    }
}
