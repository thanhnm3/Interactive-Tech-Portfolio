package com.portfolio.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BusinessException
 */
@DisplayName("BusinessException Tests")
class BusinessExceptionTest {

    @Test
    @DisplayName("Should create exception with error code and message")
    void shouldCreateExceptionWithErrorCodeAndMessage() {
        BusinessException exception = new BusinessException("ERR_001", "Business rule violated");

        assertThat(exception.getErrorCode()).isEqualTo("ERR_001");
        assertThat(exception.getMessage()).isEqualTo("Business rule violated");
    }

    @Test
    @DisplayName("Should create exception with cause")
    void shouldCreateExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        BusinessException exception = new BusinessException("ERR_001", "Business rule violated", cause);

        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
