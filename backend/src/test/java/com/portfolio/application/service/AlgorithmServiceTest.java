package com.portfolio.application.service;

import com.portfolio.application.dto.AlgorithmVisualizationResponse;
import com.portfolio.application.service.visualizer.LinkedListVisualizer;
import com.portfolio.application.service.visualizer.QueueVisualizer;
import com.portfolio.application.service.visualizer.StackVisualizer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for AlgorithmService
 */
@DisplayName("AlgorithmService Tests")
class AlgorithmServiceTest {

    private AlgorithmService algorithmService;
    private StackVisualizer stackVisualizer;
    private QueueVisualizer queueVisualizer;
    private LinkedListVisualizer linkedListVisualizer;

    @BeforeEach
    void setUp() {
        stackVisualizer = new StackVisualizer();
        queueVisualizer = new QueueVisualizer();
        linkedListVisualizer = new LinkedListVisualizer();
        algorithmService = new AlgorithmService(
            stackVisualizer,
            queueVisualizer,
            linkedListVisualizer
        );
    }

    @Test
    @DisplayName("Should execute stack operations")
    void shouldExecuteStackOperations() {
        List<String> operations = List.of("PUSH:10", "PUSH:20", "POP");

        AlgorithmVisualizationResponse response = algorithmService.executeStackOperations(operations);

        assertThat(response).isNotNull();
        assertThat(response.getAlgorithmName()).isEqualTo("Stack Operations");
    }

    @Test
    @DisplayName("Should execute queue operations")
    void shouldExecuteQueueOperations() {
        List<String> operations = List.of("ENQUEUE:A", "ENQUEUE:B", "DEQUEUE");

        AlgorithmVisualizationResponse response = algorithmService.executeQueueOperations(operations);

        assertThat(response).isNotNull();
        assertThat(response.getAlgorithmName()).isEqualTo("Queue Operations");
    }

    @Test
    @DisplayName("Should execute linked list operations")
    void shouldExecuteLinkedListOperations() {
        List<String> operations = List.of("INSERT_LAST:Node1", "INSERT_LAST:Node2");

        AlgorithmVisualizationResponse response = algorithmService.executeLinkedListOperations(operations);

        assertThat(response).isNotNull();
        assertThat(response.getAlgorithmName()).isEqualTo("LinkedList Operations");
    }

    @Test
    @DisplayName("Should get stack demo")
    void shouldGetStackDemo() {
        AlgorithmVisualizationResponse response = algorithmService.getStackDemo();

        assertThat(response).isNotNull();
        assertThat(response.getSteps()).isNotEmpty();
    }

    @Test
    @DisplayName("Should get queue demo")
    void shouldGetQueueDemo() {
        AlgorithmVisualizationResponse response = algorithmService.getQueueDemo();

        assertThat(response).isNotNull();
        assertThat(response.getSteps()).isNotEmpty();
    }

    @Test
    @DisplayName("Should get linked list demo")
    void shouldGetLinkedListDemo() {
        AlgorithmVisualizationResponse response = algorithmService.getLinkedListDemo();

        assertThat(response).isNotNull();
        assertThat(response.getSteps()).isNotEmpty();
    }

    @Test
    @DisplayName("Should get available algorithms")
    void shouldGetAvailableAlgorithms() {
        List<String> algorithms = algorithmService.getAvailableAlgorithms();

        assertThat(algorithms).contains("stack", "queue", "linkedlist");
    }
}
