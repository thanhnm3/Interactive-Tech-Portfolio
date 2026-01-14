package com.portfolio.application.service.visualizer;

import com.portfolio.application.dto.AlgorithmStepDto;
import com.portfolio.application.dto.AlgorithmVisualizationResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

/**
 * Queue data structure visualizer
 * Demonstrates FIFO (First In First Out) operations with step tracking
 */
@Component
public class QueueVisualizer {

    private Queue<Object> queue;
    private List<AlgorithmStepDto> steps;
    private int stepCounter;

    /**
     * Java code for Queue implementation
     */
    private static final String QUEUE_JAVA_CODE = """
        public class Queue<T> {
            private LinkedList<T> elements = new LinkedList<>();
            
            /**
             * Enqueue element at the rear
             * @param element - element to enqueue
             */
            public void enqueue(T element) {
                elements.addLast(element);  // O(1)
            }
            
            /**
             * Dequeue element from the front
             * @return T - front element
             */
            public T dequeue() {
                if (isEmpty()) {
                    throw new NoSuchElementException("Queue is empty");
                }
                return elements.removeFirst();  // O(1)
            }
            
            /**
             * Peek at front element without removing
             * @return T - front element
             */
            public T peek() {
                if (isEmpty()) {
                    throw new NoSuchElementException("Queue is empty");
                }
                return elements.getFirst();  // O(1)
            }
            
            /**
             * Check if queue is empty
             * @return boolean - true if empty
             */
            public boolean isEmpty() {
                return elements.isEmpty();
            }
            
            /**
             * Get queue size
             * @return int - number of elements
             */
            public int size() {
                return elements.size();
            }
        }
        """;

    /**
     * Initialize queue visualizer
     */
    public QueueVisualizer() {
        reset();
    }

    /**
     * Reset queue to empty state
     */
    public void reset() {
        this.queue = new LinkedList<>();
        this.steps = new ArrayList<>();
        this.stepCounter = 0;
    }

    /**
     * Execute enqueue operation with visualization
     * @param element - element to enqueue
     * @return AlgorithmStepDto - visualization step
     */
    public AlgorithmStepDto enqueue(Object element) {
        stepCounter++;

        queue.offer(element);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", "ENQUEUE");
        metadata.put("value", element);
        metadata.put("queueSize", queue.size());
        metadata.put("frontElement", queue.peek());
        metadata.put("rearIndex", queue.size() - 1);

        AlgorithmStepDto step = new AlgorithmStepDto.Builder()
            .stepNumber(stepCounter)
            .operation("ENQUEUE")
            .description(String.format("Enqueue element '%s' at rear. Queue size: %d", element, queue.size()))
            .currentState(new ArrayList<>(queue))
            .metadata(metadata)
            .highlightedElement(String.valueOf(queue.size() - 1))
            .isComplete(false)
            .build();

        steps.add(step);
        return step;
    }

    /**
     * Execute dequeue operation with visualization
     * @return AlgorithmStepDto - visualization step
     */
    public AlgorithmStepDto dequeue() {
        stepCounter++;

        if (queue.isEmpty()) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("operation", "DEQUEUE");
            metadata.put("error", "Queue underflow - cannot dequeue from empty queue");

            return new AlgorithmStepDto.Builder()
                .stepNumber(stepCounter)
                .operation("DEQUEUE")
                .description("Error: Cannot dequeue from empty queue (Queue Underflow)")
                .currentState(new ArrayList<>(queue))
                .metadata(metadata)
                .highlightedElement(null)
                .isComplete(false)
                .build();
        }

        Object dequeuedElement = queue.poll();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", "DEQUEUE");
        metadata.put("dequeuedValue", dequeuedElement);
        metadata.put("queueSize", queue.size());
        metadata.put("frontElement", queue.peek());

        AlgorithmStepDto step = new AlgorithmStepDto.Builder()
            .stepNumber(stepCounter)
            .operation("DEQUEUE")
            .description(String.format("Dequeue element '%s' from front. Queue size: %d", dequeuedElement, queue.size()))
            .currentState(new ArrayList<>(queue))
            .metadata(metadata)
            .highlightedElement(String.valueOf(dequeuedElement))
            .isComplete(false)
            .build();

        steps.add(step);
        return step;
    }

    /**
     * Execute peek operation with visualization
     * @return AlgorithmStepDto - visualization step
     */
    public AlgorithmStepDto peek() {
        stepCounter++;

        if (queue.isEmpty()) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("operation", "PEEK");
            metadata.put("error", "Queue is empty - nothing to peek");

            return new AlgorithmStepDto.Builder()
                .stepNumber(stepCounter)
                .operation("PEEK")
                .description("Error: Cannot peek empty queue")
                .currentState(new ArrayList<>(queue))
                .metadata(metadata)
                .highlightedElement(null)
                .isComplete(false)
                .build();
        }

        Object frontElement = queue.peek();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", "PEEK");
        metadata.put("frontValue", frontElement);
        metadata.put("queueSize", queue.size());

        AlgorithmStepDto step = new AlgorithmStepDto.Builder()
            .stepNumber(stepCounter)
            .operation("PEEK")
            .description(String.format("Peek at front element: '%s'. Queue unchanged.", frontElement))
            .currentState(new ArrayList<>(queue))
            .metadata(metadata)
            .highlightedElement("0")
            .isComplete(false)
            .build();

        steps.add(step);
        return step;
    }

    /**
     * Execute a series of operations and return complete visualization
     * @param operations - list of operations (e.g., ["ENQUEUE:A", "ENQUEUE:B", "DEQUEUE"])
     * @return AlgorithmVisualizationResponse - complete visualization
     */
    public AlgorithmVisualizationResponse executeOperations(List<String> operations) {
        reset();
        long startTime = System.nanoTime();

        for (String operation : operations) {
            String[] parts = operation.split(":");
            String op = parts[0].toUpperCase();

            switch (op) {
                case "ENQUEUE" -> {
                    if (parts.length > 1) {
                        enqueue(parts[1]);
                    }
                }
                case "DEQUEUE" -> dequeue();
                case "PEEK" -> peek();
            }
        }

        // Mark last step as complete
        if (!steps.isEmpty()) {
            AlgorithmStepDto lastStep = steps.get(steps.size() - 1);
            lastStep.setComplete(true);
        }

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        return new AlgorithmVisualizationResponse(
            "Queue Operations",
            "Queue (FIFO)",
            steps,
            executionTime,
            QUEUE_JAVA_CODE,
            "O(1) for enqueue, dequeue, peek",
            "O(n) where n is number of elements"
        );
    }

    /**
     * Get current queue state
     * @return List - current elements
     */
    public List<Object> getCurrentState() {
        return new ArrayList<>(queue);
    }

    /**
     * Get all recorded steps
     * @return List - all steps
     */
    public List<AlgorithmStepDto> getSteps() {
        return new ArrayList<>(steps);
    }
}
