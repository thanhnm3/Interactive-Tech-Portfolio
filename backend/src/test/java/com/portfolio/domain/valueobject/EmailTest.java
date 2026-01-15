package com.portfolio.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Email value object
 */
@DisplayName("Email Value Object Tests")
class EmailTest {

    @Test
    @DisplayName("Should create Email from valid string")
    void shouldCreateEmailFromValidString() {
        Email email = Email.of("test@example.com");

        assertThat(email.getValue()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should normalize email to lowercase")
    void shouldNormalizeEmailToLowercase() {
        Email email = Email.of("Test@Example.COM");

        assertThat(email.getValue()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should trim whitespace from email")
    void shouldTrimWhitespaceFromEmail() {
        Email email = Email.of("  test@example.com  ");

        assertThat(email.getValue()).isEqualTo("test@example.com");
    }

    @Test
    @DisplayName("Should extract domain from email")
    void shouldExtractDomainFromEmail() {
        Email email = Email.of("user@example.com");

        assertThat(email.getDomain()).isEqualTo("example.com");
    }

    @Test
    @DisplayName("Should extract local part from email")
    void shouldExtractLocalPartFromEmail() {
        Email email = Email.of("user@example.com");

        assertThat(email.getLocalPart()).isEqualTo("user");
    }

    @Test
    @DisplayName("Should throw exception for null email")
    void shouldThrowExceptionForNullEmail() {
        assertThatThrownBy(() -> Email.of(null))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Email cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for empty email")
    void shouldThrowExceptionForEmptyEmail() {
        assertThatThrownBy(() -> Email.of(""))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email cannot be empty");
    }

    @Test
    @DisplayName("Should throw exception for invalid email format")
    void shouldThrowExceptionForInvalidEmailFormat() {
        assertThatThrownBy(() -> Email.of("invalid-email"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Invalid email format");
    }

    @Test
    @DisplayName("Should accept valid email formats")
    void shouldAcceptValidEmailFormats() {
        Email email1 = Email.of("user@example.com");
        Email email2 = Email.of("user.name@example.co.uk");
        Email email3 = Email.of("user+tag@example.com");

        assertThat(email1.getValue()).contains("@");
        assertThat(email2.getValue()).contains("@");
        assertThat(email3.getValue()).contains("@");
    }

    @Test
    @DisplayName("Should have correct equals and hashCode")
    void shouldHaveCorrectEqualsAndHashCode() {
        Email email1 = Email.of("test@example.com");
        Email email2 = Email.of("TEST@EXAMPLE.COM");
        Email email3 = Email.of("other@example.com");

        assertThat(email1).isEqualTo(email2);
        assertThat(email1.hashCode()).isEqualTo(email2.hashCode());
        assertThat(email1).isNotEqualTo(email3);
    }

    @Test
    @DisplayName("Should return email as string")
    void shouldReturnEmailAsString() {
        Email email = Email.of("test@example.com");

        assertThat(email.toString()).isEqualTo("test@example.com");
    }
}
