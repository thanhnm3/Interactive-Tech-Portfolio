package com.portfolio.infrastructure.web.controller;

import com.portfolio.application.dto.QueryComparisonDto;
import com.portfolio.application.service.QueryPerformanceService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for DatabaseLabController
 */
@WebMvcTest(DatabaseLabController.class)
@DisplayName("DatabaseLabController Tests")
class DatabaseLabControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private QueryPerformanceService queryPerformanceService;

    @Test
    @DisplayName("Should get available scenarios")
    void shouldGetAvailableScenarios() throws Exception {
        when(queryPerformanceService.getAvailableScenarios())
            .thenReturn(List.of(
                Map.of("key", "index-usage", "name", "Index Usage", "description", "Test")
            ));

        mockMvc.perform(get("/api/v1/db-lab/scenarios"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray());
    }

    @Test
    @DisplayName("Should get scenario details")
    void shouldGetScenarioDetails() throws Exception {
        Map<String, Object> details = Map.of(
            "key", "index-usage",
            "name", "Index Usage",
            "description", "Test scenario"
        );

        when(queryPerformanceService.getScenarioDetails("index-usage"))
            .thenReturn(details);

        mockMvc.perform(get("/api/v1/db-lab/scenarios/index-usage"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.key").value("index-usage"));
    }

    @Test
    @DisplayName("Should return not found for unknown scenario")
    void shouldReturnNotFoundForUnknownScenario() throws Exception {
        when(queryPerformanceService.getScenarioDetails("unknown"))
            .thenReturn(null);

        mockMvc.perform(get("/api/v1/db-lab/scenarios/unknown"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get query comparison")
    void shouldGetQueryComparison() throws Exception {
        QueryComparisonDto comparison = new QueryComparisonDto.Builder()
            .scenarioName("Index Usage")
            .description("Test")
            .build();

        when(queryPerformanceService.getQueryComparison("index-usage"))
            .thenReturn(comparison);

        mockMvc.perform(get("/api/v1/db-lab/scenarios/index-usage/compare"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.scenarioName").exists());
    }

    @Test
    @DisplayName("Should get execution plan analysis")
    void shouldGetExecutionPlanAnalysis() throws Exception {
        Map<String, Object> analysis = Map.of(
            "plan", Map.of("type", "Index Scan"),
            "analysis", Map.of("score", 90)
        );

        when(queryPerformanceService.getExecutionPlanAnalysis("index-usage", false))
            .thenReturn(analysis);

        mockMvc.perform(get("/api/v1/db-lab/scenarios/index-usage/plan?optimized=false"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.plan").exists());
    }

    @Test
    @DisplayName("Should get showcase")
    void shouldGetShowcase() throws Exception {
        when(queryPerformanceService.getAvailableScenarios())
            .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/db-lab/showcase"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.topics").isArray());
    }

    @Test
    @DisplayName("Should get optimization tips")
    void shouldGetOptimizationTips() throws Exception {
        mockMvc.perform(get("/api/v1/db-lab/tips"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isArray())
            .andExpect(jsonPath("$[0].title").exists());
    }
}
