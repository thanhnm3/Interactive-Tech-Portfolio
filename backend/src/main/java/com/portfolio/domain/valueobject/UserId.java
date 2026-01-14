package com.portfolio.domain.valueobject;

import java.util.Objects;
import java.util.UUID;

/**
 * Value Object representing a unique user identifier
 * Immutable and self-validating
 */
public class UserId {

    private final UUID value;

    /**
     * Private constructor to enforce factory method usage
     * @param value - UUID value
     */
    private UserId(UUID value) {
        this.value = Objects.requireNonNull(value, "UserId cannot be null");
    }

    /**
     * Create a new random UserId
     * @return UserId - new instance with random UUID
     */
    public static UserId generate() {
        return new UserId(UUID.randomUUID());
    }

    /**
     * Create UserId from string representation
     * @param value - string UUID value
     * @return UserId - new instance
     */
    public static UserId of(String value) {
        return new UserId(UUID.fromString(value));
    }

    /**
     * Create UserId from UUID
     * @param value - UUID value
     * @return UserId - new instance
     */
    public static UserId of(UUID value) {
        return new UserId(value);
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
        UserId userId = (UserId) o;
        return Objects.equals(value, userId.value);
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
