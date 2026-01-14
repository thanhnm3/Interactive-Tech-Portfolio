package com.portfolio.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a unique order identifier
 * Immutable and self-validating
 */
public class OrderId {

    private final UUID value;

    /**
     * Private constructor to enforce factory method usage
     * @param value - UUID value
     */
    private OrderId(UUID value) {
        this.value = Objects.requireNonNull(value, "OrderId cannot be null");
    }

    /**
     * Create a new random OrderId
     * @return OrderId - new instance with random UUID
     */
    public static OrderId generate() {
        return new OrderId(UUID.randomUUID());
    }

    /**
     * Create OrderId from string representation
     * @param value - string UUID value
     * @return OrderId - new instance
     */
    public static OrderId of(String value) {
        return new OrderId(UUID.fromString(value));
    }

    /**
     * Create OrderId from UUID
     * @param value - UUID value
     * @return OrderId - new instance
     */
    public static OrderId of(UUID value) {
        return new OrderId(value);
    }

    /**
     * Get the UUID value
     * @return UUID - the underlying value
     */
    public UUID getValue() {
        return value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderId orderId = (OrderId) o;
        return Objects.equals(value, orderId.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
