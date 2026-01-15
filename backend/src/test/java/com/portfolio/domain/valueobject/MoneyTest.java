package com.portfolio.domain.valueobject;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Currency;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Money value object
 */
@DisplayName("Money Value Object Tests")
class MoneyTest {

    @Test
    @DisplayName("Should create Money from BigDecimal with default currency")
    void shouldCreateMoneyFromBigDecimal() {
        Money money = Money.of(BigDecimal.valueOf(1000.50));

        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000.50));
        assertThat(money.getCurrency()).isEqualTo(Currency.getInstance("JPY"));
    }

    @Test
    @DisplayName("Should create Money from double")
    void shouldCreateMoneyFromDouble() {
        Money money = Money.of(500.75);

        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(500.75));
    }

    @Test
    @DisplayName("Should create Money from long")
    void shouldCreateMoneyFromLong() {
        Money money = Money.of(1000L);

        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("Should create zero Money")
    void shouldCreateZeroMoney() {
        Money zero = Money.zero();

        assertThat(zero.getAmount()).isEqualByComparingTo(BigDecimal.ZERO);
        assertThat(zero.isZero()).isTrue();
    }

    @Test
    @DisplayName("Should create Money with custom currency")
    void shouldCreateMoneyWithCustomCurrency() {
        Money money = Money.of(BigDecimal.valueOf(100), Currency.getInstance("USD"));

        assertThat(money.getCurrency()).isEqualTo(Currency.getInstance("USD"));
    }

    @Test
    @DisplayName("Should add two Money values")
    void shouldAddMoneyValues() {
        Money money1 = Money.of(1000);
        Money money2 = Money.of(500);

        Money result = money1.add(money2);

        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1500));
    }

    @Test
    @DisplayName("Should subtract Money values")
    void shouldSubtractMoneyValues() {
        Money money1 = Money.of(1000);
        Money money2 = Money.of(300);

        Money result = money1.subtract(money2);

        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(700));
    }

    @Test
    @DisplayName("Should multiply Money by integer")
    void shouldMultiplyMoneyByInteger() {
        Money money = Money.of(100);

        Money result = money.multiply(3);

        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(300));
    }

    @Test
    @DisplayName("Should multiply Money by BigDecimal")
    void shouldMultiplyMoneyByBigDecimal() {
        Money money = Money.of(1000);

        Money result = money.multiply(BigDecimal.valueOf(0.1));

        assertThat(result.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(100));
    }

    @Test
    @DisplayName("Should throw exception when adding different currencies")
    void shouldThrowExceptionWhenAddingDifferentCurrencies() {
        Money jpy = Money.of(BigDecimal.valueOf(1000), Currency.getInstance("JPY"));
        Money usd = Money.of(BigDecimal.valueOf(10), Currency.getInstance("USD"));

        assertThatThrownBy(() -> jpy.add(usd))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("different currencies");
    }

    @Test
    @DisplayName("Should check if Money is positive")
    void shouldCheckIfMoneyIsPositive() {
        Money positive = Money.of(100);
        Money zero = Money.zero();
        Money negative = Money.of(-50);

        assertThat(positive.isPositive()).isTrue();
        assertThat(zero.isPositive()).isFalse();
        assertThat(negative.isPositive()).isFalse();
    }

    @Test
    @DisplayName("Should check if Money is negative")
    void shouldCheckIfMoneyIsNegative() {
        Money positive = Money.of(100);
        Money zero = Money.zero();
        Money negative = Money.of(-50);

        assertThat(positive.isNegative()).isFalse();
        assertThat(zero.isNegative()).isFalse();
        assertThat(negative.isNegative()).isTrue();
    }

    @Test
    @DisplayName("Should check if Money is zero")
    void shouldCheckIfMoneyIsZero() {
        Money zero = Money.zero();
        Money nonZero = Money.of(100);

        assertThat(zero.isZero()).isTrue();
        assertThat(nonZero.isZero()).isFalse();
    }

    @Test
    @DisplayName("Should compare Money values")
    void shouldCompareMoneyValues() {
        Money money1 = Money.of(1000);
        Money money2 = Money.of(500);
        Money money3 = Money.of(1000);

        assertThat(money1.isGreaterThan(money2)).isTrue();
        assertThat(money2.isLessThan(money1)).isTrue();
        assertThat(money1.isGreaterThan(money3)).isFalse();
    }

    @Test
    @DisplayName("Should round to 2 decimal places")
    void shouldRoundToTwoDecimalPlaces() {
        Money money = Money.of(BigDecimal.valueOf(100.999));

        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(101.00));
    }

    @Test
    @DisplayName("Should be immutable")
    void shouldBeImmutable() {
        Money original = Money.of(1000);
        Money added = original.add(Money.of(500));

        assertThat(original.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(added.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1500));
    }

    @Test
    @DisplayName("Should have correct equals and hashCode")
    void shouldHaveCorrectEqualsAndHashCode() {
        Money money1 = Money.of(1000);
        Money money2 = Money.of(1000);
        Money money3 = Money.of(500);

        assertThat(money1).isEqualTo(money2);
        assertThat(money1.hashCode()).isEqualTo(money2.hashCode());
        assertThat(money1).isNotEqualTo(money3);
    }

    @Test
    @DisplayName("Should throw exception when amount is null")
    void shouldThrowExceptionWhenAmountIsNull() {
        assertThatThrownBy(() -> Money.of((BigDecimal) null))
            .isInstanceOf(NullPointerException.class);
    }
}
