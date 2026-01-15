package com.portfolio.domain.strategy;

import com.portfolio.domain.entity.Order;
import com.portfolio.domain.entity.Product;
import com.portfolio.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for CreditCardPayment strategy
 */
@DisplayName("CreditCardPayment Strategy Tests")
class CreditCardPaymentTest {

    private final CreditCardPayment paymentStrategy = new CreditCardPayment();

    @Test
    @DisplayName("Should process valid credit card payment")
    void shouldProcessValidCreditCardPayment() {
        Product product = new Product("SKU-001", "Product", Money.of(5000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .withTaxRate(0.10)
            .build();

        PaymentResult result = paymentStrategy.process(order);

        assertThat(result).isNotNull();
        assertThat(result.getTransactionId()).isNotNull();
        assertThat(result.getTransactionId()).startsWith("CC-");
    }

    @Test
    @DisplayName("Should return failure for amount below minimum")
    void shouldReturnFailureForAmountBelowMinimum() {
        Product product = new Product("SKU-001", "Product", Money.of(50));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        PaymentResult result = paymentStrategy.process(order);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo("INVALID_AMOUNT");
    }

    @Test
    @DisplayName("Should return failure for amount above maximum")
    void shouldReturnFailureForAmountAboveMaximum() {
        Product product = new Product("SKU-001", "Product", Money.of(2000000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        PaymentResult result = paymentStrategy.process(order);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo("INVALID_AMOUNT");
    }

    @Test
    @DisplayName("Should check if order can be processed")
    void shouldCheckIfOrderCanBeProcessed() {
        Product validProduct = new Product("SKU-001", "Product", Money.of(5000));
        Order validOrder = new Order.Builder()
            .addItem(validProduct, 1)
            .build();

        Product invalidProduct = new Product("SKU-002", "Product", Money.of(50));
        Order invalidOrder = new Order.Builder()
            .addItem(invalidProduct, 1)
            .build();

        assertThat(paymentStrategy.canProcess(validOrder)).isTrue();
        assertThat(paymentStrategy.canProcess(invalidOrder)).isFalse();
    }

    @Test
    @DisplayName("Should return correct payment method name")
    void shouldReturnCorrectPaymentMethodName() {
        assertThat(paymentStrategy.getPaymentMethodName()).isEqualTo("CREDIT_CARD");
    }

    @Test
    @DisplayName("Should return correct processing fee rate")
    void shouldReturnCorrectProcessingFeeRate() {
        assertThat(paymentStrategy.getProcessingFeeRate()).isEqualTo(0.03);
    }

    @Test
    @DisplayName("Should calculate processing fee correctly")
    void shouldCalculateProcessingFeeCorrectly() {
        Product product = new Product("SKU-001", "Product", Money.of(10000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        PaymentResult result = paymentStrategy.process(order);

        if (result.isSuccess()) {
            assertThat(result.getProcessingFee()).isNotNull();
            BigDecimal expectedFee = BigDecimal.valueOf(10000 * 0.03);
            assertThat(result.getProcessingFee().getAmount())
                .isEqualByComparingTo(expectedFee);
        }
    }
}
