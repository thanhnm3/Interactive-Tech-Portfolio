package com.portfolio.application.service.visualizer;

import com.portfolio.application.dto.AlgorithmStepDto;
import com.portfolio.application.dto.AlgorithmVisualizationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for StackVisualizer
 */
@DisplayName("StackVisualizer Tests")
class StackVisualizerTest {

    private StackVisualizer visualizer;

    @BeforeEach
    void setUp() {
        visualizer = new StackVisualizer();
    }

    @Test
    @DisplayName("Should push element onto stack")
    void shouldPushElementOntoStack() {
        AlgorithmStepDto step = visualizer.push("10");

        assertThat(step.getOperation()).isEqualTo("PUSH");
        assertThat(step.getCurrentState()).contains("10");
        assertThat(visualizer.getCurrentState()).hasSize(1);
    }

    @Test
    @DisplayName("Should pop element from stack")
    void shouldPopElementFromStack() {
        visualizer.push("10");
        visualizer.push("20");
        AlgorithmStepDto step = visualizer.pop();

        assertThat(step.getOperation()).isEqualTo("POP");
        assertThat(step.getMetadata().get("poppedValue")).isEqualTo("20");
        assertThat(visualizer.getCurrentState()).hasSize(1);
    }

    @Test
    @DisplayName("Should return error when popping from empty stack")
    void shouldReturnErrorWhenPoppingFromEmptyStack() {
        AlgorithmStepDto step = visualizer.pop();

        assertThat(step.getOperation()).isEqualTo("POP");
        assertThat(step.getMetadata().get("error")).isNotNull();
    }

    @Test
    @DisplayName("Should peek at top element")
    void shouldPeekAtTopElement() {
        visualizer.push("10");
        visualizer.push("20");
        AlgorithmStepDto step = visualizer.peek();

        assertThat(step.getOperation()).isEqualTo("PEEK");
        assertThat(step.getMetadata().get("topValue")).isEqualTo("20");
        assertThat(visualizer.getCurrentState()).hasSize(2);
    }

    @Test
    @DisplayName("Should execute operations and return visualization")
    void shouldExecuteOperationsAndReturnVisualization() {
        List<String> operations = List.of("PUSH:10", "PUSH:20", "PEEK", "POP");

        AlgorithmVisualizationResponse response = visualizer.executeOperations(operations);

        assertThat(response).isNotNull();
        assertThat(response.getAlgorithmName()).isEqualTo("Stack Operations");
        assertThat(response.getSteps()).hasSize(4);
        assertThat(response.getSteps().get(3).isComplete()).isTrue();
    }

    @Test
    @DisplayName("Should reset stack state")
    void shouldResetStackState() {
        visualizer.push("10");
        visualizer.push("20");
        visualizer.reset();

        assertThat(visualizer.getCurrentState()).isEmpty();
        assertThat(visualizer.getSteps()).isEmpty();
    }
}
