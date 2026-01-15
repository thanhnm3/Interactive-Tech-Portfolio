package com.portfolio.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for UserId value object
 */
@DisplayName("UserId Value Object Tests")
class UserIdTest {

    @Test
    @DisplayName("Should generate new UserId")
    void shouldGenerateNewUserId() {
        UserId userId1 = UserId.generate();
        UserId userId2 = UserId.generate();

        assertThat(userId1.getValue()).isNotNull();
        assertThat(userId2.getValue()).isNotNull();
        assertThat(userId1).isNotEqualTo(userId2);
    }

    @Test
    @DisplayName("Should create UserId from string UUID")
    void shouldCreateUserIdFromStringUuid() {
        String uuidString = "550e8400-e29b-41d4-a716-446655440000";
        UserId userId = UserId.of(uuidString);

        assertThat(userId.getValue().toString()).isEqualTo(uuidString);
    }

    @Test
    @DisplayName("Should create UserId from UUID")
    void shouldCreateUserIdFromUuid() {
        UUID uuid = UUID.randomUUID();
        UserId userId = UserId.of(uuid);

        assertThat(userId.getValue()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("Should throw exception for null UUID string")
    void shouldThrowExceptionForNullUuidString() {
        assertThatThrownBy(() -> UserId.of((String) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw exception for null UUID")
    void shouldThrowExceptionForNullUuid() {
        assertThatThrownBy(() -> UserId.of((UUID) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw exception for invalid UUID string")
    void shouldThrowExceptionForInvalidUuidString() {
        assertThatThrownBy(() -> UserId.of("invalid-uuid"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should have correct equals and hashCode")
    void shouldHaveCorrectEqualsAndHashCode() {
        UUID uuid = UUID.randomUUID();
        UserId userId1 = UserId.of(uuid);
        UserId userId2 = UserId.of(uuid);
        UserId userId3 = UserId.generate();

        assertThat(userId1).isEqualTo(userId2);
        assertThat(userId1.hashCode()).isEqualTo(userId2.hashCode());
        assertThat(userId1).isNotEqualTo(userId3);
    }

    @Test
    @DisplayName("Should return UUID as string")
    void shouldReturnUuidAsString() {
        UUID uuid = UUID.randomUUID();
        UserId userId = UserId.of(uuid);

        assertThat(userId.toString()).isEqualTo(uuid.toString());
    }
}
