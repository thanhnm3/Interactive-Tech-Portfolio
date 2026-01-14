package com.portfolio.application.service;

import com.portfolio.application.dto.AlgorithmVisualizationResponse;
import com.portfolio.application.service.visualizer.LinkedListVisualizer;
import com.portfolio.application.service.visualizer.QueueVisualizer;
import com.portfolio.application.service.visualizer.StackVisualizer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service for algorithm visualization operations
 * Coordinates different data structure visualizers
 */
@Service
public class AlgorithmService {

    private final StackVisualizer stackVisualizer;
    private final QueueVisualizer queueVisualizer;
    private final LinkedListVisualizer linkedListVisualizer;

    /**
     * Constructor with dependency injection
     * @param stackVisualizer - stack visualizer component
     * @param queueVisualizer - queue visualizer component
     * @param linkedListVisualizer - linked list visualizer component
     */
    public AlgorithmService(StackVisualizer stackVisualizer,
                           QueueVisualizer queueVisualizer,
                           LinkedListVisualizer linkedListVisualizer) {
        this.stackVisualizer = stackVisualizer;
        this.queueVisualizer = queueVisualizer;
        this.linkedListVisualizer = linkedListVisualizer;
    }

    /**
     * Execute stack operations with visualization
     * @param operations - list of operations (e.g., ["PUSH:5", "PUSH:10", "POP"])
     * @return AlgorithmVisualizationResponse - complete visualization
     */
    public AlgorithmVisualizationResponse executeStackOperations(List<String> operations) {
        return stackVisualizer.executeOperations(operations);
    }

    /**
     * Execute queue operations with visualization
     * @param operations - list of operations (e.g., ["ENQUEUE:A", "ENQUEUE:B", "DEQUEUE"])
     * @return AlgorithmVisualizationResponse - complete visualization
     */
    public AlgorithmVisualizationResponse executeQueueOperations(List<String> operations) {
        return queueVisualizer.executeOperations(operations);
    }

    /**
     * Execute linked list operations with visualization
     * @param operations - list of operations
     * @return AlgorithmVisualizationResponse - complete visualization
     */
    public AlgorithmVisualizationResponse executeLinkedListOperations(List<String> operations) {
        return linkedListVisualizer.executeOperations(operations);
    }

    /**
     * Get demo visualization for stack
     * @return AlgorithmVisualizationResponse - demo with sample operations
     */
    public AlgorithmVisualizationResponse getStackDemo() {
        List<String> demoOperations = List.of(
            "PUSH:10",
            "PUSH:20",
            "PUSH:30",
            "PEEK",
            "POP",
            "POP",
            "PUSH:40",
            "PEEK"
        );
        return executeStackOperations(demoOperations);
    }

    /**
     * Get demo visualization for queue
     * @return AlgorithmVisualizationResponse - demo with sample operations
     */
    public AlgorithmVisualizationResponse getQueueDemo() {
        List<String> demoOperations = List.of(
            "ENQUEUE:A",
            "ENQUEUE:B",
            "ENQUEUE:C",
            "PEEK",
            "DEQUEUE",
            "DEQUEUE",
            "ENQUEUE:D",
            "PEEK"
        );
        return executeQueueOperations(demoOperations);
    }

    /**
     * Get demo visualization for linked list
     * @return AlgorithmVisualizationResponse - demo with sample operations
     */
    public AlgorithmVisualizationResponse getLinkedListDemo() {
        List<String> demoOperations = List.of(
            "INSERT_LAST:Node1",
            "INSERT_LAST:Node2",
            "INSERT_LAST:Node3",
            "INSERT_FIRST:HeadNode",
            "INSERT_AT:2:MiddleNode",
            "SEARCH:Node2",
            "DELETE_AT:1",
            "SEARCH:Node1"
        );
        return executeLinkedListOperations(demoOperations);
    }

    /**
     * Get all available algorithm types
     * @return List - available algorithm names
     */
    public List<String> getAvailableAlgorithms() {
        return List.of("stack", "queue", "linkedlist");
    }
}
