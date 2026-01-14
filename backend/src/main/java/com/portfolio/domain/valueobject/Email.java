package com.portfolio.domain.valueobject;

import java.util.Objects;
import java.util.regex.Pattern;

/**
 * Value Object representing an email address
 * Immutable and self-validating with email format validation
 */
public class Email {

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
        "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"
    );

    private final String value;

    /**
     * Private constructor with validation
     * @param value - email string value
     */
    private Email(String value) {
        Objects.requireNonNull(value, "Email cannot be null");

        String trimmedValue = value.trim().toLowerCase();

        if (trimmedValue.isEmpty()) {
            throw new IllegalArgumentException("Email cannot be empty");
        }

        if (!EMAIL_PATTERN.matcher(trimmedValue).matches()) {
            throw new IllegalArgumentException("Invalid email format: " + value);
        }

        this.value = trimmedValue;
    }

    /**
     * Create Email from string value
     * @param value - email string
     * @return Email - new instance
     */
    public static Email of(String value) {
        return new Email(value);
    }

    /**
     * Get the email string value
     * @return String - the email address
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the domain part of the email
     * @return String - domain after @ symbol
     */
    public String getDomain() {
        return value.substring(value.indexOf('@') + 1);
    }

    /**
     * Get the local part of the email (before @)
     * @return String - local part before @ symbol
     */
    public String getLocalPart() {
        return value.substring(0, value.indexOf('@'));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Email email = (Email) o;
        return Objects.equals(value, email.value);
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return value;
    }
}
