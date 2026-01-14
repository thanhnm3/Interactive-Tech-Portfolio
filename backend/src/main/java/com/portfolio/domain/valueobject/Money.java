package com.portfolio.domain.valueobject;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Currency;
import java.util.Objects;

/**
 * Value Object representing monetary amount with currency
 * Immutable and supports arithmetic operations
 */
public class Money {

    private static final Currency DEFAULT_CURRENCY = Currency.getInstance("JPY");
    private static final int SCALE = 2;
    private static final RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;

    private final BigDecimal amount;
    private final Currency currency;

    /**
     * Private constructor
     * @param amount - decimal amount
     * @param currency - currency type
     */
    private Money(BigDecimal amount, Currency currency) {
        Objects.requireNonNull(amount, "Amount cannot be null");
        Objects.requireNonNull(currency, "Currency cannot be null");

        this.amount = amount.setScale(SCALE, ROUNDING_MODE);
        this.currency = currency;
    }

    /**
     * Create Money with default currency (JPY)
     * @param amount - decimal amount
     * @return Money - new instance
     */
    public static Money of(BigDecimal amount) {
        return new Money(amount, DEFAULT_CURRENCY);
    }

    /**
     * Create Money with specified currency
     * @param amount - decimal amount
     * @param currency - currency type
     * @return Money - new instance
     */
    public static Money of(BigDecimal amount, Currency currency) {
        return new Money(amount, currency);
    }

    /**
     * Create Money from double value
     * @param amount - double amount
     * @return Money - new instance
     */
    public static Money of(double amount) {
        return new Money(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
    }

    /**
     * Create Money from long value (for JPY which has no decimals)
     * @param amount - long amount
     * @return Money - new instance
     */
    public static Money of(long amount) {
        return new Money(BigDecimal.valueOf(amount), DEFAULT_CURRENCY);
    }

    /**
     * Create zero amount Money
     * @return Money - zero value
     */
    public static Money zero() {
        return new Money(BigDecimal.ZERO, DEFAULT_CURRENCY);
    }

    /**
     * Get the amount value
     * @return BigDecimal - the amount
     */
    public BigDecimal getAmount() {
        return amount;
    }

    /**
     * Get the currency
     * @return Currency - the currency type
     */
    public Currency getCurrency() {
        return currency;
    }

    /**
     * Add two Money values
     * @param other - Money to add
     * @return Money - sum result
     */
    public Money add(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.add(other.amount), this.currency);
    }

    /**
     * Subtract Money value
     * @param other - Money to subtract
     * @return Money - difference result
     */
    public Money subtract(Money other) {
        validateSameCurrency(other);
        return new Money(this.amount.subtract(other.amount), this.currency);
    }

    /**
     * Multiply by quantity
     * @param multiplier - quantity to multiply by
     * @return Money - product result
     */
    public Money multiply(int multiplier) {
        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currency);
    }

    /**
     * Multiply by decimal factor
     * @param multiplier - factor to multiply by
     * @return Money - product result
     */
    public Money multiply(BigDecimal multiplier) {
        return new Money(this.amount.multiply(multiplier), this.currency);
    }

    /**
     * Check if amount is positive
     * @return boolean - true if positive
     */
    public boolean isPositive() {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }

    /**
     * Check if amount is negative
     * @return boolean - true if negative
     */
    public boolean isNegative() {
        return amount.compareTo(BigDecimal.ZERO) < 0;
    }

    /**
     * Check if amount is zero
     * @return boolean - true if zero
     */
    public boolean isZero() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }

    /**
     * Check if this amount is greater than other
     * @param other - Money to compare
     * @return boolean - true if greater
     */
    public boolean isGreaterThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) > 0;
    }

    /**
     * Check if this amount is less than other
     * @param other - Money to compare
     * @return boolean - true if less
     */
    public boolean isLessThan(Money other) {
        validateSameCurrency(other);
        return this.amount.compareTo(other.amount) < 0;
    }

    /**
     * Validate that currencies match for operations
     * @param other - Money to validate against
     */
    private void validateSameCurrency(Money other) {
        if (!this.currency.equals(other.currency)) {
            throw new IllegalArgumentException(
                "Cannot perform operation with different currencies: " +
                this.currency + " and " + other.currency
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Money money = (Money) o;
        return amount.compareTo(money.amount) == 0 &&
               Objects.equals(currency, money.currency);
    }

    @Override
    public int hashCode() {
        return Objects.hash(amount, currency);
    }

    @Override
    public String toString() {
        return currency.getSymbol() + " " + amount.toPlainString();
    }
}
