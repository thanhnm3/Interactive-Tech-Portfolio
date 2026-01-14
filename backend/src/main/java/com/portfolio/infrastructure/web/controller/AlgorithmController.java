package com.portfolio.infrastructure.web.controller;

import com.portfolio.application.dto.AlgorithmVisualizationResponse;
import com.portfolio.application.service.AlgorithmService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for algorithm visualization endpoints
 * Provides APIs for data structure operations and demos
 */
@RestController
@RequestMapping("/api/v1/algorithms")
@CrossOrigin(origins = "${app.cors.allowed-origins:http://localhost:3000}")
public class AlgorithmController {

    private final AlgorithmService algorithmService;

    /**
     * Constructor with dependency injection
     * @param algorithmService - algorithm service
     */
    public AlgorithmController(AlgorithmService algorithmService) {
        this.algorithmService = algorithmService;
    }

    /**
     * Get list of available algorithms
     * @return ResponseEntity - list of algorithm names
     */
    @GetMapping
    public ResponseEntity<Map<String, Object>> getAvailableAlgorithms() {
        List<String> algorithms = algorithmService.getAvailableAlgorithms();

        return ResponseEntity.ok(Map.of(
            "algorithms", algorithms,
            "message", "Available data structure visualizations"
        ));
    }

    /**
     * Execute stack operations
     * @param request - request body with operations list
     * @return ResponseEntity - visualization response
     */
    @PostMapping("/stack/execute")
    public ResponseEntity<AlgorithmVisualizationResponse> executeStackOperations(
            @RequestBody Map<String, List<String>> request) {

        List<String> operations = request.get("operations");

        if (operations == null || operations.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        AlgorithmVisualizationResponse response = algorithmService.executeStackOperations(operations);
        return ResponseEntity.ok(response);
    }

    /**
     * Get stack demo visualization
     * @return ResponseEntity - demo visualization
     */
    @GetMapping("/stack/demo")
    public ResponseEntity<AlgorithmVisualizationResponse> getStackDemo() {
        AlgorithmVisualizationResponse response = algorithmService.getStackDemo();
        return ResponseEntity.ok(response);
    }

    /**
     * Execute queue operations
     * @param request - request body with operations list
     * @return ResponseEntity - visualization response
     */
    @PostMapping("/queue/execute")
    public ResponseEntity<AlgorithmVisualizationResponse> executeQueueOperations(
            @RequestBody Map<String, List<String>> request) {

        List<String> operations = request.get("operations");

        if (operations == null || operations.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        AlgorithmVisualizationResponse response = algorithmService.executeQueueOperations(operations);
        return ResponseEntity.ok(response);
    }

    /**
     * Get queue demo visualization
     * @return ResponseEntity - demo visualization
     */
    @GetMapping("/queue/demo")
    public ResponseEntity<AlgorithmVisualizationResponse> getQueueDemo() {
        AlgorithmVisualizationResponse response = algorithmService.getQueueDemo();
        return ResponseEntity.ok(response);
    }

    /**
     * Execute linked list operations
     * @param request - request body with operations list
     * @return ResponseEntity - visualization response
     */
    @PostMapping("/linkedlist/execute")
    public ResponseEntity<AlgorithmVisualizationResponse> executeLinkedListOperations(
            @RequestBody Map<String, List<String>> request) {

        List<String> operations = request.get("operations");

        if (operations == null || operations.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        AlgorithmVisualizationResponse response = algorithmService.executeLinkedListOperations(operations);
        return ResponseEntity.ok(response);
    }

    /**
     * Get linked list demo visualization
     * @return ResponseEntity - demo visualization
     */
    @GetMapping("/linkedlist/demo")
    public ResponseEntity<AlgorithmVisualizationResponse> getLinkedListDemo() {
        AlgorithmVisualizationResponse response = algorithmService.getLinkedListDemo();
        return ResponseEntity.ok(response);
    }

    /**
     * Get all demos for showcase
     * @return ResponseEntity - all demo visualizations
     */
    @GetMapping("/demos")
    public ResponseEntity<Map<String, AlgorithmVisualizationResponse>> getAllDemos() {
        return ResponseEntity.ok(Map.of(
            "stack", algorithmService.getStackDemo(),
            "queue", algorithmService.getQueueDemo(),
            "linkedlist", algorithmService.getLinkedListDemo()
        ));
    }
}
