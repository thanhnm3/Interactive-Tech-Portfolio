package com.portfolio.infrastructure.web.controller;

import com.portfolio.application.dto.CodeSnippetDto;
import com.portfolio.application.dto.SqlCaptureDto;
import com.portfolio.application.service.CodeDisplayService;
import com.portfolio.application.service.DesignPatternDocService;
import com.portfolio.application.service.DesignPatternDocService.PatternDoc;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Map;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Unit tests for CodeLabController
 */
@WebMvcTest(CodeLabController.class)
@DisplayName("CodeLabController Tests")
class CodeLabControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private CodeDisplayService codeDisplayService;

    @MockBean
    private DesignPatternDocService patternDocService;

    @Test
    @DisplayName("Should get available resources")
    void shouldGetAvailableResources() throws Exception {
        when(codeDisplayService.getAvailableSnippetKeys())
            .thenReturn(List.of("jpa-repository"));
        when(codeDisplayService.getAvailableCategories())
            .thenReturn(List.of("persistence"));
        when(patternDocService.getAvailablePatternKeys())
            .thenReturn(List.of("builder"));
        when(patternDocService.getAvailablePatternTypes())
            .thenReturn(List.of("Creational"));

        mockMvc.perform(get("/api/v1/code-lab"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.codeSnippets").exists())
            .andExpect(jsonPath("$.patterns").exists());
    }

    @Test
    @DisplayName("Should get code snippet by key")
    void shouldGetCodeSnippetByKey() throws Exception {
        CodeSnippetDto snippet = new CodeSnippetDto.Builder()
            .title("JPA Repository")
            .language("java")
            .sourceCode("code")
            .build();

        when(codeDisplayService.getCodeSnippet("jpa-repository"))
            .thenReturn(snippet);

        mockMvc.perform(get("/api/v1/code-lab/snippets/jpa-repository"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.title").value("JPA Repository"));
    }

    @Test
    @DisplayName("Should return not found for unknown snippet")
    void shouldReturnNotFoundForUnknownSnippet() throws Exception {
        when(codeDisplayService.getCodeSnippet("unknown"))
            .thenReturn(null);

        mockMvc.perform(get("/api/v1/code-lab/snippets/unknown"))
            .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Should get all code snippets")
    void shouldGetAllCodeSnippets() throws Exception {
        when(codeDisplayService.getAllCodeSnippets())
            .thenReturn(Map.of());

        mockMvc.perform(get("/api/v1/code-lab/snippets"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should get snippets by category")
    void shouldGetSnippetsByCategory() throws Exception {
        when(codeDisplayService.getSnippetsByCategory("persistence"))
            .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/code-lab/snippets/category/persistence"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should get pattern doc by key")
    void shouldGetPatternDocByKey() throws Exception {
        PatternDoc doc = new PatternDoc(
            "Builder Pattern",
            "Creational",
            "Intent",
            "Problem",
            "Solution",
            List.of(),
            "Example",
            "Code",
            List.of()
        );

        when(patternDocService.getPatternDoc("builder"))
            .thenReturn(doc);

        mockMvc.perform(get("/api/v1/code-lab/patterns/builder"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Builder Pattern"));
    }

    @Test
    @DisplayName("Should start SQL capture")
    void shouldStartSqlCapture() throws Exception {
        doNothing().when(codeDisplayService).startSqlCapture();

        mockMvc.perform(post("/api/v1/code-lab/sql-capture/start"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("started"));

        verify(codeDisplayService).startSqlCapture();
    }

    @Test
    @DisplayName("Should stop SQL capture")
    void shouldStopSqlCapture() throws Exception {
        doNothing().when(codeDisplayService).stopSqlCapture();
        when(codeDisplayService.getCapturedSql())
            .thenReturn(List.of());

        mockMvc.perform(post("/api/v1/code-lab/sql-capture/stop"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("stopped"));

        verify(codeDisplayService).stopSqlCapture();
    }

    @Test
    @DisplayName("Should get captured SQL")
    void shouldGetCapturedSql() throws Exception {
        when(codeDisplayService.getCapturedSql())
            .thenReturn(List.of());

        mockMvc.perform(get("/api/v1/code-lab/sql-capture"))
            .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Should clear captured SQL")
    void shouldClearCapturedSql() throws Exception {
        doNothing().when(codeDisplayService).clearCapturedSql();

        mockMvc.perform(delete("/api/v1/code-lab/sql-capture"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("cleared"));

        verify(codeDisplayService).clearCapturedSql();
    }
}
