package com.portfolio.infrastructure.web.controller;

import com.portfolio.application.dto.AlgorithmVisualizationResponse;
import com.portfolio.application.service.AlgorithmService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for AlgorithmController
 */
@WebMvcTest(AlgorithmController.class)
@DisplayName("AlgorithmController Tests")
class AlgorithmControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AlgorithmService algorithmService;

    @Test
    @DisplayName("Should get available algorithms")
    void shouldGetAvailableAlgorithms() throws Exception {
        when(algorithmService.getAvailableAlgorithms())
            .thenReturn(List.of("stack", "queue", "linkedlist"));

        mockMvc.perform(get("/api/v1/algorithms"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.algorithms").isArray())
            .andExpect(jsonPath("$.message").exists());
    }

    @Test
    @DisplayName("Should execute stack operations")
    void shouldExecuteStackOperations() throws Exception {
        AlgorithmVisualizationResponse response = new AlgorithmVisualizationResponse(
            "Stack Operations",
            "Stack",
            List.of(),
            10L,
            "code",
            "O(1)",
            "O(n)"
        );

        when(algorithmService.executeStackOperations(anyList()))
            .thenReturn(response);

        Map<String, List<String>> request = Map.of(
            "operations", List.of("PUSH:10", "PUSH:20", "POP")
        );

        mockMvc.perform(post("/api/v1/algorithms/stack/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.algorithmName").value("Stack Operations"));
    }

    @Test
    @DisplayName("Should return bad request for empty operations")
    void shouldReturnBadRequestForEmptyOperations() throws Exception {
        Map<String, List<String>> request = Map.of("operations", List.of());

        mockMvc.perform(post("/api/v1/algorithms/stack/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Should get stack demo")
    void shouldGetStackDemo() throws Exception {
        AlgorithmVisualizationResponse response = new AlgorithmVisualizationResponse(
            "Stack Demo",
            "Stack",
            List.of(),
            10L,
            "code",
            "O(1)",
            "O(n)"
        );

        when(algorithmService.getStackDemo()).thenReturn(response);

        mockMvc.perform(get("/api/v1/algorithms/stack/demo"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.algorithmName").value("Stack Demo"));
    }

    @Test
    @DisplayName("Should execute queue operations")
    void shouldExecuteQueueOperations() throws Exception {
        AlgorithmVisualizationResponse response = new AlgorithmVisualizationResponse(
            "Queue Operations",
            "Queue",
            List.of(),
            10L,
            "code",
            "O(1)",
            "O(n)"
        );

        when(algorithmService.executeQueueOperations(anyList()))
            .thenReturn(response);

        Map<String, List<String>> request = Map.of(
            "operations", List.of("ENQUEUE:A", "ENQUEUE:B", "DEQUEUE")
        );

        mockMvc.perform(post("/api/v1/algorithms/queue/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should execute linked list operations")
    void shouldExecuteLinkedListOperations() throws Exception {
        AlgorithmVisualizationResponse response = new AlgorithmVisualizationResponse(
            "LinkedList Operations",
            "LinkedList",
            List.of(),
            10L,
            "code",
            "O(1)",
            "O(n)"
        );

        when(algorithmService.executeLinkedListOperations(anyList()))
            .thenReturn(response);

        Map<String, List<String>> request = Map.of(
            "operations", List.of("INSERT_LAST:Node1", "INSERT_LAST:Node2")
        );

        mockMvc.perform(post("/api/v1/algorithms/linkedlist/execute")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(request)))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should get all demos")
    void shouldGetAllDemos() throws Exception {
        AlgorithmVisualizationResponse stackDemo = new AlgorithmVisualizationResponse(
            "Stack Demo", "Stack", List.of(), 10L, "code", "O(1)", "O(n)");
        AlgorithmVisualizationResponse queueDemo = new AlgorithmVisualizationResponse(
            "Queue Demo", "Queue", List.of(), 10L, "code", "O(1)", "O(n)");
        AlgorithmVisualizationResponse linkedListDemo = new AlgorithmVisualizationResponse(
            "LinkedList Demo", "LinkedList", List.of(), 10L, "code", "O(1)", "O(n)");

        when(algorithmService.getStackDemo()).thenReturn(stackDemo);
        when(algorithmService.getQueueDemo()).thenReturn(queueDemo);
        when(algorithmService.getLinkedListDemo()).thenReturn(linkedListDemo);

        mockMvc.perform(get("/api/v1/algorithms/demos"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.stack").exists())
            .andExpect(jsonPath("$.queue").exists())
            .andExpect(jsonPath("$.linkedlist").exists());
    }
}
