package com.portfolio.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for OrderId value object
 */
@DisplayName("OrderId Value Object Tests")
class OrderIdTest {

    @Test
    @DisplayName("Should generate new OrderId")
    void shouldGenerateNewOrderId() {
        OrderId orderId1 = OrderId.generate();
        OrderId orderId2 = OrderId.generate();

        assertThat(orderId1.getValue()).isNotNull();
        assertThat(orderId2.getValue()).isNotNull();
        assertThat(orderId1).isNotEqualTo(orderId2);
    }

    @Test
    @DisplayName("Should create OrderId from string UUID")
    void shouldCreateOrderIdFromStringUuid() {
        String uuidString = "550e8400-e29b-41d4-a716-446655440000";
        OrderId orderId = OrderId.of(uuidString);

        assertThat(orderId.getValue().toString()).isEqualTo(uuidString);
    }

    @Test
    @DisplayName("Should create OrderId from UUID")
    void shouldCreateOrderIdFromUuid() {
        UUID uuid = UUID.randomUUID();
        OrderId orderId = OrderId.of(uuid);

        assertThat(orderId.getValue()).isEqualTo(uuid);
    }

    @Test
    @DisplayName("Should throw exception for null UUID string")
    void shouldThrowExceptionForNullUuidString() {
        assertThatThrownBy(() -> OrderId.of((String) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw exception for null UUID")
    void shouldThrowExceptionForNullUuid() {
        assertThatThrownBy(() -> OrderId.of((UUID) null))
            .isInstanceOf(NullPointerException.class);
    }

    @Test
    @DisplayName("Should throw exception for invalid UUID string")
    void shouldThrowExceptionForInvalidUuidString() {
        assertThatThrownBy(() -> OrderId.of("invalid-uuid"))
            .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    @DisplayName("Should have correct equals and hashCode")
    void shouldHaveCorrectEqualsAndHashCode() {
        UUID uuid = UUID.randomUUID();
        OrderId orderId1 = OrderId.of(uuid);
        OrderId orderId2 = OrderId.of(uuid);
        OrderId orderId3 = OrderId.generate();

        assertThat(orderId1).isEqualTo(orderId2);
        assertThat(orderId1.hashCode()).isEqualTo(orderId2.hashCode());
        assertThat(orderId1).isNotEqualTo(orderId3);
    }

    @Test
    @DisplayName("Should return UUID as string")
    void shouldReturnUuidAsString() {
        UUID uuid = UUID.randomUUID();
        OrderId orderId = OrderId.of(uuid);

        assertThat(orderId.toString()).isEqualTo(uuid.toString());
    }
}
