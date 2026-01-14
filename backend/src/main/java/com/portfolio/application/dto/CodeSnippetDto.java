package com.portfolio.application.dto;

import java.util.List;
import java.util.Map;

/**
 * DTO for code snippet display
 * Contains source code and metadata for visualization
 */
public class CodeSnippetDto {

    private String title;
    private String language;
    private String sourceCode;
    private String description;
    private List<String> highlightedLines;
    private Map<String, String> annotations;
    private String category;

    /**
     * Default constructor
     */
    public CodeSnippetDto() {
    }

    /**
     * Create code snippet with all properties
     * @param title - snippet title
     * @param language - programming language
     * @param sourceCode - actual code
     * @param description - explanation
     * @param highlightedLines - lines to highlight
     * @param annotations - line annotations
     * @param category - snippet category
     */
    public CodeSnippetDto(String title, String language, String sourceCode,
                          String description, List<String> highlightedLines,
                          Map<String, String> annotations, String category) {
        this.title = title;
        this.language = language;
        this.sourceCode = sourceCode;
        this.description = description;
        this.highlightedLines = highlightedLines;
        this.annotations = annotations;
        this.category = category;
    }

    /**
     * Get title
     * @return String - title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Set title
     * @param title - title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get language
     * @return String - language
     */
    public String getLanguage() {
        return language;
    }

    /**
     * Set language
     * @param language - language
     */
    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     * Get source code
     * @return String - code
     */
    public String getSourceCode() {
        return sourceCode;
    }

    /**
     * Set source code
     * @param sourceCode - code
     */
    public void setSourceCode(String sourceCode) {
        this.sourceCode = sourceCode;
    }

    /**
     * Get description
     * @return String - description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set description
     * @param description - description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Get highlighted lines
     * @return List - line numbers to highlight
     */
    public List<String> getHighlightedLines() {
        return highlightedLines;
    }

    /**
     * Set highlighted lines
     * @param highlightedLines - line numbers to highlight
     */
    public void setHighlightedLines(List<String> highlightedLines) {
        this.highlightedLines = highlightedLines;
    }

    /**
     * Get annotations
     * @return Map - line annotations
     */
    public Map<String, String> getAnnotations() {
        return annotations;
    }

    /**
     * Set annotations
     * @param annotations - line annotations
     */
    public void setAnnotations(Map<String, String> annotations) {
        this.annotations = annotations;
    }

    /**
     * Get category
     * @return String - category
     */
    public String getCategory() {
        return category;
    }

    /**
     * Set category
     * @param category - category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * Builder for CodeSnippetDto
     */
    public static class Builder {
        private String title;
        private String language;
        private String sourceCode;
        private String description;
        private List<String> highlightedLines;
        private Map<String, String> annotations;
        private String category;

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder language(String language) {
            this.language = language;
            return this;
        }

        public Builder sourceCode(String sourceCode) {
            this.sourceCode = sourceCode;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder highlightedLines(List<String> highlightedLines) {
            this.highlightedLines = highlightedLines;
            return this;
        }

        public Builder annotations(Map<String, String> annotations) {
            this.annotations = annotations;
            return this;
        }

        public Builder category(String category) {
            this.category = category;
            return this;
        }

        public CodeSnippetDto build() {
            return new CodeSnippetDto(title, language, sourceCode, description,
                highlightedLines, annotations, category);
        }
    }
}
