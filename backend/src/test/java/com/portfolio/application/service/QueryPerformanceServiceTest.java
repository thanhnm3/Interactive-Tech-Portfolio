package com.portfolio.application.service;

import com.portfolio.application.dto.ExecutionPlanDto;
import com.portfolio.application.dto.QueryComparisonDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Unit tests for QueryPerformanceService
 */
@DisplayName("QueryPerformanceService Tests")
class QueryPerformanceServiceTest {

    private QueryPerformanceService service;
    private ExecutionPlanService executionPlanService;

    @BeforeEach
    void setUp() {
        executionPlanService = mock(ExecutionPlanService.class);
        service = new QueryPerformanceService(executionPlanService);
    }

    @Test
    @DisplayName("Should get available scenarios")
    void shouldGetAvailableScenarios() {
        List<Map<String, String>> scenarios = service.getAvailableScenarios();

        assertThat(scenarios).isNotEmpty();
        assertThat(scenarios).anyMatch(s -> s.get("key").equals("index-usage"));
    }

    @Test
    @DisplayName("Should get scenario details")
    void shouldGetScenarioDetails() {
        Map<String, Object> details = service.getScenarioDetails("index-usage");

        assertThat(details).isNotNull();
        assertThat(details.get("key")).isEqualTo("index-usage");
        assertThat(details.get("name")).isNotNull();
    }

    @Test
    @DisplayName("Should return null for unknown scenario")
    void shouldReturnNullForUnknownScenario() {
        Map<String, Object> details = service.getScenarioDetails("unknown");

        assertThat(details).isNull();
    }

    @Test
    @DisplayName("Should get query comparison")
    void shouldGetQueryComparison() {
        // Create mock ExecutionPlanDto with valid values for unoptimized query
        ExecutionPlanDto unoptPlan = new ExecutionPlanDto();
        unoptPlan.setPlanType("Seq Scan");
        unoptPlan.setTotalCost(100.0);
        unoptPlan.setActualTime(10.0);
        unoptPlan.setRowsReturned(100L);
        unoptPlan.setNodes(List.of());

        // Create mock ExecutionPlanDto with valid values for optimized query
        ExecutionPlanDto optPlan = new ExecutionPlanDto();
        optPlan.setPlanType("Index Scan");
        optPlan.setTotalCost(10.0);
        optPlan.setActualTime(1.0);
        optPlan.setRowsReturned(100L);
        optPlan.setNodes(List.of());

        // Mock the service to return different plans based on the boolean parameter
        when(executionPlanService.parseExecutionPlan(anyString(), anyBoolean()))
            .thenAnswer(invocation -> {
                Boolean isOptimized = invocation.getArgument(1);
                return isOptimized ? optPlan : unoptPlan;
            });

        QueryComparisonDto comparison = service.getQueryComparison("index-usage");

        assertThat(comparison).isNotNull();
        assertThat(comparison.getScenarioName()).isNotNull();
    }
}
