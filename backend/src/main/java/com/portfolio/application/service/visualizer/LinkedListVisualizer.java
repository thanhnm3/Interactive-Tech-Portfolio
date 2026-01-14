package com.portfolio.application.service.visualizer;

import com.portfolio.application.dto.AlgorithmStepDto;
import com.portfolio.application.dto.AlgorithmVisualizationResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LinkedList data structure visualizer
 * Demonstrates linked list operations with step tracking and node visualization
 */
@Component
public class LinkedListVisualizer {

    /**
     * Internal node representation for visualization
     */
    public static class Node {
        Object value;
        int index;

        Node(Object value, int index) {
            this.value = value;
            this.index = index;
        }

        @Override
        public String toString() {
            return String.format("{value: %s, index: %d}", value, index);
        }
    }

    private List<Node> linkedList;
    private List<AlgorithmStepDto> steps;
    private int stepCounter;

    /**
     * Java code for LinkedList implementation
     */
    private static final String LINKED_LIST_JAVA_CODE = """
        public class LinkedList<T> {
            private Node<T> head;
            private Node<T> tail;
            private int size;
            
            private static class Node<T> {
                T data;
                Node<T> next;
                Node<T> prev;
                
                Node(T data) {
                    this.data = data;
                }
            }
            
            /**
             * Insert at the beginning
             * @param data - element to insert
             */
            public void insertFirst(T data) {
                Node<T> newNode = new Node<>(data);
                if (head == null) {
                    head = tail = newNode;
                } else {
                    newNode.next = head;
                    head.prev = newNode;
                    head = newNode;
                }
                size++;
            }
            
            /**
             * Insert at the end
             * @param data - element to insert
             */
            public void insertLast(T data) {
                Node<T> newNode = new Node<>(data);
                if (tail == null) {
                    head = tail = newNode;
                } else {
                    tail.next = newNode;
                    newNode.prev = tail;
                    tail = newNode;
                }
                size++;
            }
            
            /**
             * Insert at specific position
             * @param index - position to insert at
             * @param data - element to insert
             */
            public void insertAt(int index, T data) {
                if (index < 0 || index > size) {
                    throw new IndexOutOfBoundsException();
                }
                if (index == 0) {
                    insertFirst(data);
                } else if (index == size) {
                    insertLast(data);
                } else {
                    Node<T> current = head;
                    for (int i = 0; i < index; i++) {
                        current = current.next;
                    }
                    Node<T> newNode = new Node<>(data);
                    newNode.prev = current.prev;
                    newNode.next = current;
                    current.prev.next = newNode;
                    current.prev = newNode;
                    size++;
                }
            }
            
            /**
             * Delete at specific position
             * @param index - position to delete
             * @return T - deleted element
             */
            public T deleteAt(int index) {
                if (index < 0 || index >= size) {
                    throw new IndexOutOfBoundsException();
                }
                Node<T> current = head;
                for (int i = 0; i < index; i++) {
                    current = current.next;
                }
                T data = current.data;
                if (current.prev != null) {
                    current.prev.next = current.next;
                } else {
                    head = current.next;
                }
                if (current.next != null) {
                    current.next.prev = current.prev;
                } else {
                    tail = current.prev;
                }
                size--;
                return data;
            }
            
            /**
             * Search for element
             * @param data - element to find
             * @return int - index or -1 if not found
             */
            public int search(T data) {
                Node<T> current = head;
                int index = 0;
                while (current != null) {
                    if (current.data.equals(data)) {
                        return index;
                    }
                    current = current.next;
                    index++;
                }
                return -1;
            }
        }
        """;

    /**
     * Initialize LinkedList visualizer
     */
    public LinkedListVisualizer() {
        reset();
    }

    /**
     * Reset list to empty state
     */
    public void reset() {
        this.linkedList = new ArrayList<>();
        this.steps = new ArrayList<>();
        this.stepCounter = 0;
    }

    /**
     * Convert internal list to visualization format
     * @return List - node representations
     */
    private List<Object> toVisualizationState() {
        List<Object> state = new ArrayList<>();
        for (int i = 0; i < linkedList.size(); i++) {
            Map<String, Object> nodeMap = new HashMap<>();
            nodeMap.put("index", i);
            nodeMap.put("value", linkedList.get(i).value);
            nodeMap.put("hasNext", i < linkedList.size() - 1);
            nodeMap.put("hasPrev", i > 0);
            state.add(nodeMap);
        }
        return state;
    }

    /**
     * Update node indices after modification
     */
    private void reindex() {
        for (int i = 0; i < linkedList.size(); i++) {
            linkedList.get(i).index = i;
        }
    }

    /**
     * Insert at beginning with visualization
     * @param value - value to insert
     * @return AlgorithmStepDto - visualization step
     */
    public AlgorithmStepDto insertFirst(Object value) {
        stepCounter++;

        Node newNode = new Node(value, 0);
        linkedList.add(0, newNode);
        reindex();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", "INSERT_FIRST");
        metadata.put("value", value);
        metadata.put("listSize", linkedList.size());
        metadata.put("insertedAt", 0);

        AlgorithmStepDto step = new AlgorithmStepDto.Builder()
            .stepNumber(stepCounter)
            .operation("INSERT_FIRST")
            .description(String.format("Insert '%s' at head. List size: %d", value, linkedList.size()))
            .currentState(toVisualizationState())
            .metadata(metadata)
            .highlightedElement("0")
            .isComplete(false)
            .build();

        steps.add(step);
        return step;
    }

    /**
     * Insert at end with visualization
     * @param value - value to insert
     * @return AlgorithmStepDto - visualization step
     */
    public AlgorithmStepDto insertLast(Object value) {
        stepCounter++;

        int position = linkedList.size();
        Node newNode = new Node(value, position);
        linkedList.add(newNode);

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", "INSERT_LAST");
        metadata.put("value", value);
        metadata.put("listSize", linkedList.size());
        metadata.put("insertedAt", position);

        AlgorithmStepDto step = new AlgorithmStepDto.Builder()
            .stepNumber(stepCounter)
            .operation("INSERT_LAST")
            .description(String.format("Insert '%s' at tail (position %d). List size: %d", value, position, linkedList.size()))
            .currentState(toVisualizationState())
            .metadata(metadata)
            .highlightedElement(String.valueOf(position))
            .isComplete(false)
            .build();

        steps.add(step);
        return step;
    }

    /**
     * Insert at specific index with visualization
     * @param index - position to insert
     * @param value - value to insert
     * @return AlgorithmStepDto - visualization step
     */
    public AlgorithmStepDto insertAt(int index, Object value) {
        stepCounter++;

        if (index < 0 || index > linkedList.size()) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("operation", "INSERT_AT");
            metadata.put("error", "Index out of bounds: " + index);

            return new AlgorithmStepDto.Builder()
                .stepNumber(stepCounter)
                .operation("INSERT_AT")
                .description(String.format("Error: Index %d is out of bounds (size: %d)", index, linkedList.size()))
                .currentState(toVisualizationState())
                .metadata(metadata)
                .highlightedElement(null)
                .isComplete(false)
                .build();
        }

        Node newNode = new Node(value, index);
        linkedList.add(index, newNode);
        reindex();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", "INSERT_AT");
        metadata.put("value", value);
        metadata.put("index", index);
        metadata.put("listSize", linkedList.size());

        AlgorithmStepDto step = new AlgorithmStepDto.Builder()
            .stepNumber(stepCounter)
            .operation("INSERT_AT")
            .description(String.format("Insert '%s' at position %d. List size: %d", value, index, linkedList.size()))
            .currentState(toVisualizationState())
            .metadata(metadata)
            .highlightedElement(String.valueOf(index))
            .isComplete(false)
            .build();

        steps.add(step);
        return step;
    }

    /**
     * Delete at specific index with visualization
     * @param index - position to delete
     * @return AlgorithmStepDto - visualization step
     */
    public AlgorithmStepDto deleteAt(int index) {
        stepCounter++;

        if (index < 0 || index >= linkedList.size()) {
            Map<String, Object> metadata = new HashMap<>();
            metadata.put("operation", "DELETE_AT");
            metadata.put("error", "Index out of bounds: " + index);

            return new AlgorithmStepDto.Builder()
                .stepNumber(stepCounter)
                .operation("DELETE_AT")
                .description(String.format("Error: Index %d is out of bounds (size: %d)", index, linkedList.size()))
                .currentState(toVisualizationState())
                .metadata(metadata)
                .highlightedElement(null)
                .isComplete(false)
                .build();
        }

        Node deletedNode = linkedList.remove(index);
        reindex();

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", "DELETE_AT");
        metadata.put("deletedValue", deletedNode.value);
        metadata.put("deletedIndex", index);
        metadata.put("listSize", linkedList.size());

        AlgorithmStepDto step = new AlgorithmStepDto.Builder()
            .stepNumber(stepCounter)
            .operation("DELETE_AT")
            .description(String.format("Delete node at position %d (value: '%s'). List size: %d", index, deletedNode.value, linkedList.size()))
            .currentState(toVisualizationState())
            .metadata(metadata)
            .highlightedElement(String.valueOf(deletedNode.value))
            .isComplete(false)
            .build();

        steps.add(step);
        return step;
    }

    /**
     * Search for value with visualization
     * @param value - value to search for
     * @return AlgorithmStepDto - visualization step
     */
    public AlgorithmStepDto search(Object value) {
        stepCounter++;

        int foundIndex = -1;
        for (int i = 0; i < linkedList.size(); i++) {
            if (linkedList.get(i).value.equals(value)) {
                foundIndex = i;
                break;
            }
        }

        Map<String, Object> metadata = new HashMap<>();
        metadata.put("operation", "SEARCH");
        metadata.put("searchValue", value);
        metadata.put("foundIndex", foundIndex);
        metadata.put("found", foundIndex != -1);

        String description = foundIndex != -1
            ? String.format("Found '%s' at position %d", value, foundIndex)
            : String.format("Value '%s' not found in list", value);

        AlgorithmStepDto step = new AlgorithmStepDto.Builder()
            .stepNumber(stepCounter)
            .operation("SEARCH")
            .description(description)
            .currentState(toVisualizationState())
            .metadata(metadata)
            .highlightedElement(foundIndex != -1 ? String.valueOf(foundIndex) : null)
            .isComplete(false)
            .build();

        steps.add(step);
        return step;
    }

    /**
     * Execute a series of operations and return complete visualization
     * @param operations - list of operations
     * @return AlgorithmVisualizationResponse - complete visualization
     */
    public AlgorithmVisualizationResponse executeOperations(List<String> operations) {
        reset();
        long startTime = System.nanoTime();

        for (String operation : operations) {
            String[] parts = operation.split(":");
            String op = parts[0].toUpperCase();

            switch (op) {
                case "INSERT_FIRST" -> {
                    if (parts.length > 1) {
                        insertFirst(parts[1]);
                    }
                }
                case "INSERT_LAST" -> {
                    if (parts.length > 1) {
                        insertLast(parts[1]);
                    }
                }
                case "INSERT_AT" -> {
                    if (parts.length > 2) {
                        insertAt(Integer.parseInt(parts[1]), parts[2]);
                    }
                }
                case "DELETE_AT" -> {
                    if (parts.length > 1) {
                        deleteAt(Integer.parseInt(parts[1]));
                    }
                }
                case "SEARCH" -> {
                    if (parts.length > 1) {
                        search(parts[1]);
                    }
                }
            }
        }

        // Mark last step as complete
        if (!steps.isEmpty()) {
            AlgorithmStepDto lastStep = steps.get(steps.size() - 1);
            lastStep.setComplete(true);
        }

        long executionTime = (System.nanoTime() - startTime) / 1_000_000;

        return new AlgorithmVisualizationResponse(
            "LinkedList Operations",
            "Doubly Linked List",
            steps,
            executionTime,
            LINKED_LIST_JAVA_CODE,
            "O(1) for insert first/last, O(n) for insert/delete at index",
            "O(n) where n is number of elements"
        );
    }

    /**
     * Get current list state
     * @return List - current nodes
     */
    public List<Object> getCurrentState() {
        return toVisualizationState();
    }

    /**
     * Get all recorded steps
     * @return List - all steps
     */
    public List<AlgorithmStepDto> getSteps() {
        return new ArrayList<>(steps);
    }
}
