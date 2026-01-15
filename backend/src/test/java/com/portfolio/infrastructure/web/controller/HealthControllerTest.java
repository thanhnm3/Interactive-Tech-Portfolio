package com.portfolio.infrastructure.web.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Unit tests for HealthController
 */
@WebMvcTest(HealthController.class)
@DisplayName("HealthController Tests")
class HealthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    @DisplayName("Should return application status")
    void shouldReturnApplicationStatus() throws Exception {
        mockMvc.perform(get("/api/v1/status"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("UP"))
            .andExpect(jsonPath("$.application").value("Portfolio Backend"));
    }

    @Test
    @DisplayName("Should return API documentation links")
    void shouldReturnApiDocumentationLinks() throws Exception {
        mockMvc.perform(get("/api/v1/docs"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.swagger").exists())
            .andExpect(jsonPath("$.openapi").exists());
    }

    @Test
    @DisplayName("Should return showcase summary")
    void shouldReturnShowcaseSummary() throws Exception {
        mockMvc.perform(get("/api/v1/showcase"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").exists())
            .andExpect(jsonPath("$.features").exists());
    }
}
