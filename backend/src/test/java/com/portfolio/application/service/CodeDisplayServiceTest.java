package com.portfolio.application.service;

import com.portfolio.application.dto.CodeSnippetDto;
import com.portfolio.application.dto.SqlCaptureDto;
import com.portfolio.infrastructure.persistence.HibernateQueryInterceptor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

/**
 * Unit tests for CodeDisplayService
 */
@DisplayName("CodeDisplayService Tests")
class CodeDisplayServiceTest {

    private CodeDisplayService service;
    private HibernateQueryInterceptor queryInterceptor;

    @BeforeEach
    void setUp() {
        queryInterceptor = mock(HibernateQueryInterceptor.class);
        service = new CodeDisplayService(queryInterceptor);
    }

    @Test
    @DisplayName("Should get code snippet by key")
    void shouldGetCodeSnippetByKey() {
        CodeSnippetDto snippet = service.getCodeSnippet("jpa-repository");

        assertThat(snippet).isNotNull();
        assertThat(snippet.getTitle()).isNotNull();
    }

    @Test
    @DisplayName("Should get all code snippets")
    void shouldGetAllCodeSnippets() {
        var snippets = service.getAllCodeSnippets();

        assertThat(snippets).isNotEmpty();
    }

    @Test
    @DisplayName("Should get snippets by category")
    void shouldGetSnippetsByCategory() {
        List<CodeSnippetDto> snippets = service.getSnippetsByCategory("persistence");

        assertThat(snippets).isNotEmpty();
    }

    @Test
    @DisplayName("Should start SQL capture")
    void shouldStartSqlCapture() {
        service.startSqlCapture();

        verify(queryInterceptor).startCapturing();
    }

    @Test
    @DisplayName("Should stop SQL capture")
    void shouldStopSqlCapture() {
        service.stopSqlCapture();

        verify(queryInterceptor).stopCapturing();
    }

    @Test
    @DisplayName("Should get captured SQL")
    void shouldGetCapturedSql() {
        when(queryInterceptor.getCapturedQueries())
            .thenReturn(List.of(new SqlCaptureDto.Builder().sql("SELECT * FROM products").build()));

        List<SqlCaptureDto> captured = service.getCapturedSql();

        assertThat(captured).isNotEmpty();
    }

    @Test
    @DisplayName("Should clear captured SQL")
    void shouldClearCapturedSql() {
        service.clearCapturedSql();

        verify(queryInterceptor).clearCapturedQueries();
    }
}
