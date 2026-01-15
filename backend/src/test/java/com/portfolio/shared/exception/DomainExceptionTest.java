package com.portfolio.shared.exception;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for DomainException
 */
@DisplayName("DomainException Tests")
class DomainExceptionTest {

    @Test
    @DisplayName("Should create exception with message")
    void shouldCreateExceptionWithMessage() {
        DomainException exception = new DomainException("Domain rule violated");

        assertThat(exception.getMessage()).isEqualTo("Domain rule violated");
        assertThat(exception.getErrorCode()).isEqualTo("DOMAIN_ERROR");
    }

    @Test
    @DisplayName("Should create exception with message and error code")
    void shouldCreateExceptionWithMessageAndErrorCode() {
        DomainException exception = new DomainException("Domain rule violated", "CUSTOM_CODE");

        assertThat(exception.getMessage()).isEqualTo("Domain rule violated");
        assertThat(exception.getErrorCode()).isEqualTo("CUSTOM_CODE");
    }

    @Test
    @DisplayName("Should create exception with cause")
    void shouldCreateExceptionWithCause() {
        Throwable cause = new RuntimeException("Root cause");
        DomainException exception = new DomainException("Domain rule violated", cause);

        assertThat(exception.getCause()).isEqualTo(cause);
    }
}
