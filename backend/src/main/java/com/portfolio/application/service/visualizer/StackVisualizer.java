package com.portfolio.application.service.visualizer;

import com.portfolio.application.dto.AlgorithmStepDto;
import com.portfolio.application.dto.AlgorithmVisualizationResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Stack data structure visualizer
 * Demonstrates LIFO (Last In First Out) operations with step tracking
 */
@Component
public class StackVisualizer {

    private List<Object> stack;
    private List<AlgorithmStepDto> steps;
    private int stepCounter;

    /**
     * Java code for Stack implementation
     */
    private static final String STACK_JAVA_CODE = """
        public class Stack<T> {
            private List<T> elements = new ArrayList<>();
            
            /**
             * Push element onto the stack
             * @param element - element to push
             */
            public void push(T element) {
                elements.add(element);  // O(1) amortized
            }
            
            /**
             * Pop element from the stack
             * @return T - top element
             */
            public T pop() {
                if (isEmpty()) {
                    throw new EmptyStackException();
                }
                return elements.remove(elements.size() - 1);  // O(1)
            }
            
            /**
             * Peek at top element without removing
             * @return T - top element
             */
            public T peek() {
                if (isEmpty()) {
                    throw new EmptyStackException();
                }
                return elements.get(elements.size() - 1);  // O(1)
            }
            
            /**
             * Check if stack is empty
             * @return boolean - true if empty
             */
            public boolean isEmpty() {
                return elements.isEmpty();
            }
            
            /**
             * Get stack size
             * @return int - number of elements
             */
            public int size() {
                return elements.size();
            }
        }
        """;

    /**
     * Initialize stack visualizer
     */
    public StackVisualizer() {
        reset();
    }

    /**
     * Reset stack to empty state
     */
    public void reset() {
        this.stack = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.stepCounter = 0;
    }

    /**
     * Execute push operation with visualization
     * @param element - element to push
     * @return AlgorithmStepDto - visualization step
     */
    public AlgorithmStepDto push(Object element) {
        stepCounter++;

        stack.add(element);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", "PUSH");
        metadata.put("value", element);
        metadata.put("stackSize", stack.size());
        metadata.put("topIndex", stack.size() - 1);

        AlgorithmStepDto step = new AlgorithmStepDto.Builder()
            .stepNumber(stepCounter)
            .operation("PUSH")
            .description(String.format("Push element '%s' onto the stack. Stack size: %d", element, stack.size()))
            .currentState(new ArrayList<>(stack))
            .metadata(metadata)
            .highlightedElement(String.valueOf(stack.size() - 1))
            .isComplete(false)
            .build();

        steps.add(step);
        return step;
    }

    /**
     * Execute pop operation with visualization
     * @return AlgorithmStepDto - visualization step
     */
    public AlgorithmStepDto pop() {
        stepCounter++;

        if (stack.isEmpty()) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("operation", "POP");
            metadata.put("error", "Stack underflow - cannot pop from empty stack");

            return new AlgorithmStepDto.Builder()
                .stepNumber(stepCounter)
                .operation("POP")
                .description("Error: Cannot pop from empty stack (Stack Underflow)")
                .currentState(new ArrayList<>(stack))
                .metadata(metadata)
                .highlightedElement(null)
                .isComplete(false)
                .build();
        }

        Object poppedElement = stack.remove(stack.size() - 1);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", "POP");
        metadata.put("poppedValue", poppedElement);
        metadata.put("stackSize", stack.size());
        metadata.put("topIndex", stack.isEmpty() ? -1 : stack.size() - 1);

        AlgorithmStepDto step = new AlgorithmStepDto.Builder()
            .stepNumber(stepCounter)
            .operation("POP")
            .description(String.format("Pop element '%s' from the stack. Stack size: %d", poppedElement, stack.size()))
            .currentState(new ArrayList<>(stack))
            .metadata(metadata)
            .highlightedElement(String.valueOf(poppedElement))
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

        if (stack.isEmpty()) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("operation", "PEEK");
            metadata.put("error", "Stack is empty - nothing to peek");

            return new AlgorithmStepDto.Builder()
                .stepNumber(stepCounter)
                .operation("PEEK")
                .description("Error: Cannot peek empty stack")
                .currentState(new ArrayList<>(stack))
                .metadata(metadata)
                .highlightedElement(null)
                .isComplete(false)
                .build();
        }

        Object topElement = stack.get(stack.size() - 1);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", "PEEK");
        metadata.put("topValue", topElement);
        metadata.put("stackSize", stack.size());
        metadata.put("topIndex", stack.size() - 1);

        AlgorithmStepDto step = new AlgorithmStepDto.Builder()
            .stepNumber(stepCounter)
            .operation("PEEK")
            .description(String.format("Peek at top element: '%s'. Stack unchanged.", topElement))
            .currentState(new ArrayList<>(stack))
            .metadata(metadata)
            .highlightedElement(String.valueOf(stack.size() - 1))
            .isComplete(false)
            .build();

        steps.add(step);
        return step;
    }

    /**
     * Execute a series of operations and return complete visualization
     * @param operations - list of operations (e.g., ["PUSH:5", "PUSH:10", "POP"])
     * @return AlgorithmVisualizationResponse - complete visualization
     */
    public AlgorithmVisualizationResponse executeOperations(List<String> operations) {
        reset();
        long startTime = System.nanoTime();

        for (String operation : operations) {
            String[] parts = operation.split(":");
            String op = parts[0].toUpperCase();

            switch (op) {
                case "PUSH" -> {
                    if (parts.length > 1) {
                        push(parts[1]);
                    }
                }
                case "POP" -> pop();
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
            "Stack Operations",
            "Stack (LIFO)",
            steps,
            executionTime,
            STACK_JAVA_CODE,
            "O(1) for push, pop, peek",
            "O(n) where n is number of elements"
        );
    }

    /**
     * Get current stack state
     * @return List - current elements
     */
    public List<Object> getCurrentState() {
        return new ArrayList<>(stack);
    }

    /**
     * Get all recorded steps
     * @return List - all steps
     */
    public List<AlgorithmStepDto> getSteps() {
        return new ArrayList<>(steps);
    }
}
