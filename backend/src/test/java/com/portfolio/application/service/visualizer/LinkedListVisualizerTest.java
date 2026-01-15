package com.portfolio.application.service.visualizer;

import com.portfolio.application.dto.AlgorithmVisualizationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for LinkedListVisualizer
 */
@DisplayName("LinkedListVisualizer Tests")
class LinkedListVisualizerTest {

    private LinkedListVisualizer visualizer;

    @BeforeEach
    void setUp() {
        visualizer = new LinkedListVisualizer();
    }

    @Test
    @DisplayName("Should insert at first position")
    void shouldInsertAtFirstPosition() {
        var step = visualizer.insertFirst("Node1");

        assertThat(step.getOperation()).isEqualTo("INSERT_FIRST");
        assertThat(visualizer.getCurrentState()).hasSize(1);
    }

    @Test
    @DisplayName("Should insert at last position")
    void shouldInsertAtLastPosition() {
        visualizer.insertLast("Node1");
        var step = visualizer.insertLast("Node2");

        assertThat(step.getOperation()).isEqualTo("INSERT_LAST");
        assertThat(visualizer.getCurrentState()).hasSize(2);
    }

    @Test
    @DisplayName("Should insert at specific index")
    void shouldInsertAtSpecificIndex() {
        visualizer.insertLast("Node1");
        visualizer.insertLast("Node2");
        var step = visualizer.insertAt(1, "Middle");

        assertThat(step.getOperation()).isEqualTo("INSERT_AT");
        assertThat(visualizer.getCurrentState()).hasSize(3);
    }

    @Test
    @DisplayName("Should delete at specific index")
    void shouldDeleteAtSpecificIndex() {
        visualizer.insertLast("Node1");
        visualizer.insertLast("Node2");
        var step = visualizer.deleteAt(0);

        assertThat(step.getOperation()).isEqualTo("DELETE_AT");
        assertThat(visualizer.getCurrentState()).hasSize(1);
    }

    @Test
    @DisplayName("Should search for value")
    void shouldSearchForValue() {
        visualizer.insertLast("Node1");
        visualizer.insertLast("Node2");
        var step = visualizer.search("Node2");

        assertThat(step.getOperation()).isEqualTo("SEARCH");
        assertThat(step.getMetadata().get("found")).isEqualTo(true);
    }

    @Test
    @DisplayName("Should execute operations and return visualization")
    void shouldExecuteOperationsAndReturnVisualization() {
        List<String> operations = List.of(
            "INSERT_LAST:Node1",
            "INSERT_LAST:Node2",
            "INSERT_FIRST:Head",
            "SEARCH:Node2"
        );

        AlgorithmVisualizationResponse response = visualizer.executeOperations(operations);

        assertThat(response).isNotNull();
        assertThat(response.getAlgorithmName()).isEqualTo("LinkedList Operations");
        assertThat(response.getSteps()).hasSize(4);
    }
}
