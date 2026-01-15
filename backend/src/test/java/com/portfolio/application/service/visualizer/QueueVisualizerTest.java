package com.portfolio.application.service.visualizer;

import com.portfolio.application.dto.AlgorithmVisualizationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for QueueVisualizer
 */
@DisplayName("QueueVisualizer Tests")
class QueueVisualizerTest {

    private QueueVisualizer visualizer;

    @BeforeEach
    void setUp() {
        visualizer = new QueueVisualizer();
    }

    @Test
    @DisplayName("Should enqueue element")
    void shouldEnqueueElement() {
        var step = visualizer.enqueue("A");

        assertThat(step.getOperation()).isEqualTo("ENQUEUE");
        assertThat(visualizer.getCurrentState()).contains("A");
    }

    @Test
    @DisplayName("Should dequeue element in FIFO order")
    void shouldDequeueElementInFifoOrder() {
        visualizer.enqueue("A");
        visualizer.enqueue("B");
        var step = visualizer.dequeue();

        assertThat(step.getOperation()).isEqualTo("DEQUEUE");
        assertThat(step.getMetadata().get("dequeuedValue")).isEqualTo("A");
        assertThat(visualizer.getCurrentState()).contains("B");
    }

    @Test
    @DisplayName("Should return error when dequeuing from empty queue")
    void shouldReturnErrorWhenDequeuingFromEmptyQueue() {
        var step = visualizer.dequeue();

        assertThat(step.getMetadata().get("error")).isNotNull();
    }

    @Test
    @DisplayName("Should execute operations and return visualization")
    void shouldExecuteOperationsAndReturnVisualization() {
        List<String> operations = List.of("ENQUEUE:A", "ENQUEUE:B", "PEEK", "DEQUEUE");

        AlgorithmVisualizationResponse response = visualizer.executeOperations(operations);

        assertThat(response).isNotNull();
        assertThat(response.getAlgorithmName()).isEqualTo("Queue Operations");
        assertThat(response.getSteps()).hasSize(4);
    }
}
