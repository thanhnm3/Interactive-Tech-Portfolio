package com.portfolio.application.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO for algorithm visualization step data
 * Contains state and metadata for each step in algorithm execution
 */
public class AlgorithmStepDto {

    private int stepNumber;
    private String operation;
    private String description;
    private List<Object> currentState;
    private Map<String, Object> metadata;
    private String highlightedElement;
    private boolean isComplete;

    /**
     * Default constructor
     */
    public AlgorithmStepDto() {
    }

    /**
     * Create algorithm step with all properties
     * @param stepNumber - step sequence number
     * @param operation - operation name (PUSH, POP, etc.)
     * @param description - human-readable description
     * @param currentState - data structure state after operation
     * @param metadata - additional visualization data
     * @param highlightedElement - element to highlight in UI
     * @param isComplete - whether this is the final step
     */
    public AlgorithmStepDto(int stepNumber, String operation, String description,
                            List<Object> currentState, Map<String, Object> metadata,
                            String highlightedElement, boolean isComplete) {
        this.stepNumber = stepNumber;
        this.operation = operation;
        this.description = description;
        this.currentState = currentState;
        this.metadata = metadata;
        this.highlightedElement = highlightedElement;
        this.isComplete = isComplete;
    }

    /**
     * Get step number
     * @return int - sequence number
     */
    public int getStepNumber() {
        return stepNumber;
    }

    /**
     * Set step number
     * @param stepNumber - sequence number
     */
    public void setStepNumber(int stepNumber) {
        this.stepNumber = stepNumber;
    }

    /**
     * Get operation name
     * @return String - operation
     */
    public String getOperation() {
        return operation;
    }

    /**
     * Set operation name
     * @param operation - operation
     */
    public void setOperation(String operation) {
        this.operation = operation;
    }

    /**
     * Get description
     * @return String - human-readable description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description
     * @param description - human-readable description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get current state
     * @return List - data structure contents
     */
    public List<Object> getCurrentState() {
        return currentState;
    }

    /**
     * Set current state
     * @param currentState - data structure contents
     */
    public void setCurrentState(List<Object> currentState) {
        this.currentState = currentState;
    }

    /**
     * Get metadata
     * @return Map - additional data
     */
    public Map<String, Object> getMetadata() {
        return metadata;
    }

    /**
     * Set metadata
     * @param metadata - additional data
     */
    public void setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
    }

    /**
     * Get highlighted element
     * @return String - element to highlight
     */
    public String getHighlightedElement() {
        return highlightedElement;
    }

    /**
     * Set highlighted element
     * @param highlightedElement - element to highlight
     */
    public void setHighlightedElement(String highlightedElement) {
        this.highlightedElement = highlightedElement;
    }

    /**
     * Check if step is complete
     * @return boolean - true if final step
     */
    public boolean isComplete() {
        return isComplete;
    }

    /**
     * Set completion status
     * @param isComplete - true if final step
     */
    public void setComplete(boolean isComplete) {
        this.isComplete = isComplete;
    }

    /**
     * Builder for creating AlgorithmStepDto
     */
    public static class Builder {
        private int stepNumber;
        private String operation;
        private String description;
        private List<Object> currentState;
        private Map<String, Object> metadata;
        private String highlightedElement;
        private boolean isComplete;

        public Builder stepNumber(int stepNumber) {
            this.stepNumber = stepNumber;
            return this;
        }

        public Builder operation(String operation) {
            this.operation = operation;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder currentState(List<Object> currentState) {
            this.currentState = currentState;
            return this;
        }

        public Builder metadata(Map<String, Object> metadata) {
            this.metadata = metadata;
            return this;
        }

        public Builder highlightedElement(String highlightedElement) {
            this.highlightedElement = highlightedElement;
            return this;
        }

        public Builder isComplete(boolean isComplete) {
            this.isComplete = isComplete;
            return this;
        }

        public AlgorithmStepDto build() {
            return new AlgorithmStepDto(stepNumber, operation, description,
                currentState, metadata, highlightedElement, isComplete);
        }
    }
}
