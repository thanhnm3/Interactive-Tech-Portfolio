package com.portfolio.application.service;

import com.portfolio.application.dto.ExecutionPlanDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for ExecutionPlanService
 */
@DisplayName("ExecutionPlanService Tests")
class ExecutionPlanServiceTest {

    private ExecutionPlanService service;

    @BeforeEach
    void setUp() {
        service = new ExecutionPlanService();
    }

    @Test
    @DisplayName("Should parse execution plan for optimized query")
    void shouldParseExecutionPlanForOptimizedQuery() {
        String sql = "SELECT * FROM products WHERE category_id = ?";
        ExecutionPlanDto plan = service.parseExecutionPlan(sql, true);

        assertThat(plan).isNotNull();
        assertThat(plan.getPlanType()).isEqualTo("Index Scan");
        assertThat(plan.getTotalCost()).isLessThan(100);
    }

    @Test
    @DisplayName("Should parse execution plan for unoptimized query")
    void shouldParseExecutionPlanForUnoptimizedQuery() {
        String sql = "SELECT * FROM products WHERE category_id = ?";
        ExecutionPlanDto plan = service.parseExecutionPlan(sql, false);

        assertThat(plan).isNotNull();
        assertThat(plan.getPlanType()).isEqualTo("Sequential Scan");
        assertThat(plan.getTotalCost()).isGreaterThan(100);
    }

    @Test
    @DisplayName("Should analyze execution plan")
    void shouldAnalyzeExecutionPlan() {
        String sql = "SELECT * FROM products";
        ExecutionPlanDto plan = service.parseExecutionPlan(sql, false);
        Map<String, Object> analysis = service.analyzePlan(plan);

        assertThat(analysis).isNotNull();
        assertThat(analysis.get("performanceScore")).isNotNull();
        assertThat(analysis.get("issues")).isNotNull();
    }

    @Test
    @DisplayName("Should compare execution plans")
    void shouldCompareExecutionPlans() {
        String sql = "SELECT * FROM products";
        ExecutionPlanDto plan1 = service.parseExecutionPlan(sql, false);
        ExecutionPlanDto plan2 = service.parseExecutionPlan(sql, true);

        Map<String, Object> comparison = service.comparePlans(plan1, plan2);

        assertThat(comparison).isNotNull();
        assertThat(comparison.get("isImproved")).isNotNull();
    }
}
