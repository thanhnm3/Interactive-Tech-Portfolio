package com.portfolio.application.dto;

import java.util.List;

/**
 * Response DTO for algorithm visualization
 * Contains complete visualization data with all steps
 */
public class AlgorithmVisualizationResponse {

    private String algorithmName;
    private String dataStructure;
    private List<AlgorithmStepDto> steps;
    private int totalSteps;
    private long executionTimeMs;
    private String javaCode;
    private String timeComplexity;
    private String spaceComplexity;

    /**
     * Default constructor
     */
    public AlgorithmVisualizationResponse() {
    }

    /**
     * Create response with all properties
     * @param algorithmName - name of algorithm
     * @param dataStructure - data structure type
     * @param steps - list of visualization steps
     * @param executionTimeMs - execution time
     * @param javaCode - source code
     * @param timeComplexity - time complexity notation
     * @param spaceComplexity - space complexity notation
     */
    public AlgorithmVisualizationResponse(String algorithmName, String dataStructure,
                                          List<AlgorithmStepDto> steps, long executionTimeMs,
                                          String javaCode, String timeComplexity,
                                          String spaceComplexity) {
        this.algorithmName = algorithmName;
        this.dataStructure = dataStructure;
        this.steps = steps;
        this.totalSteps = steps != null ? steps.size() : 0;
        this.executionTimeMs = executionTimeMs;
        this.javaCode = javaCode;
        this.timeComplexity = timeComplexity;
        this.spaceComplexity = spaceComplexity;
    }

    /**
     * Get algorithm name
     * @return String - algorithm name
     */
    public String getAlgorithmName() {
        return algorithmName;
    }

    /**
     * Set algorithm name
     * @param algorithmName - algorithm name
     */
    public void setAlgorithmName(String algorithmName) {
        this.algorithmName = algorithmName;
    }

    /**
     * Get data structure type
     * @return String - data structure
     */
    public String getDataStructure() {
        return dataStructure;
    }

    /**
     * Set data structure type
     * @param dataStructure - data structure
     */
    public void setDataStructure(String dataStructure) {
        this.dataStructure = dataStructure;
    }

    /**
     * Get visualization steps
     * @return List - all steps
     */
    public List<AlgorithmStepDto> getSteps() {
        return steps;
    }

    /**
     * Set visualization steps
     * @param steps - all steps
     */
    public void setSteps(List<AlgorithmStepDto> steps) {
        this.steps = steps;
        this.totalSteps = steps != null ? steps.size() : 0;
    }

    /**
     * Get total step count
     * @return int - total steps
     */
    public int getTotalSteps() {
        return totalSteps;
    }

    /**
     * Get execution time
     * @return long - time in milliseconds
     */
    public long getExecutionTimeMs() {
        return executionTimeMs;
    }

    /**
     * Set execution time
     * @param executionTimeMs - time in milliseconds
     */
    public void setExecutionTimeMs(long executionTimeMs) {
        this.executionTimeMs = executionTimeMs;
    }

    /**
     * Get Java source code
     * @return String - Java code
     */
    public String getJavaCode() {
        return javaCode;
    }

    /**
     * Set Java source code
     * @param javaCode - Java code
     */
    public void setJavaCode(String javaCode) {
        this.javaCode = javaCode;
    }

    /**
     * Get time complexity
     * @return String - Big-O notation
     */
    public String getTimeComplexity() {
        return timeComplexity;
    }

    /**
     * Set time complexity
     * @param timeComplexity - Big-O notation
     */
    public void setTimeComplexity(String timeComplexity) {
        this.timeComplexity = timeComplexity;
    }

    /**
     * Get space complexity
     * @return String - Big-O notation
     */
    public String getSpaceComplexity() {
        return spaceComplexity;
    }

    /**
     * Set space complexity
     * @param spaceComplexity - Big-O notation
     */
    public void setSpaceComplexity(String spaceComplexity) {
        this.spaceComplexity = spaceComplexity;
    }
}
