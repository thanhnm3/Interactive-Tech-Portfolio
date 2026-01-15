package com.portfolio.application.service;

import com.portfolio.application.service.DesignPatternDocService.PatternDoc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DesignPatternDocService
 */
@DisplayName("DesignPatternDocService Tests")
class DesignPatternDocServiceTest {

    private DesignPatternDocService service;

    @BeforeEach
    void setUp() {
        service = new DesignPatternDocService();
    }

    @Test
    @DisplayName("Should get pattern doc by key")
    void shouldGetPatternDocByKey() {
        PatternDoc doc = service.getPatternDoc("builder");

        assertThat(doc).isNotNull();
        assertThat(doc.name()).isEqualTo("Builder Pattern");
        assertThat(doc.type()).isEqualTo("Creational");
    }

    @Test
    @DisplayName("Should return null for unknown pattern")
    void shouldReturnNullForUnknownPattern() {
        PatternDoc doc = service.getPatternDoc("unknown");

        assertThat(doc).isNull();
    }

    @Test
    @DisplayName("Should get all pattern docs")
    void shouldGetAllPatternDocs() {
        Map<String, PatternDoc> docs = service.getAllPatternDocs();

        assertThat(docs).isNotEmpty();
        assertThat(docs).containsKey("builder");
        assertThat(docs).containsKey("factory");
        assertThat(docs).containsKey("strategy");
    }

    @Test
    @DisplayName("Should get patterns by type")
    void shouldGetPatternsByType() {
        List<PatternDoc> creational = service.getPatternsByType("Creational");

        assertThat(creational).isNotEmpty();
        assertThat(creational).anyMatch(p -> p.name().contains("Builder"));
    }

    @Test
    @DisplayName("Should get available pattern keys")
    void shouldGetAvailablePatternKeys() {
        List<String> keys = service.getAvailablePatternKeys();

        assertThat(keys).isNotEmpty();
        assertThat(keys).contains("builder", "factory", "strategy");
    }

    @Test
    @DisplayName("Should get available pattern types")
    void shouldGetAvailablePatternTypes() {
        List<String> types = service.getAvailablePatternTypes();

        assertThat(types).isNotEmpty();
        assertThat(types).contains("Creational", "Behavioral");
    }

    @Test
    @DisplayName("Should search patterns by keyword")
    void shouldSearchPatternsByKeyword() {
        List<PatternDoc> results = service.searchPatterns("builder");

        assertThat(results).isNotEmpty();
        assertThat(results).anyMatch(p -> p.name().contains("Builder"));
    }
}
