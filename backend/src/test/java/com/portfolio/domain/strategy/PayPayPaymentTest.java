package com.portfolio.domain.strategy;

import com.portfolio.domain.entity.Order;
import com.portfolio.domain.entity.Product;
import com.portfolio.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for PayPayPayment strategy
 */
@DisplayName("PayPayPayment Strategy Tests")
class PayPayPaymentTest {

    private final PayPayPayment paymentStrategy = new PayPayPayment();

    @Test
    @DisplayName("Should process valid PayPay payment")
    void shouldProcessValidPayPayPayment() {
        Product product = new Product("SKU-001", "Product", Money.of(10000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .withTaxRate(0.10)
            .build();

        PaymentResult result = paymentStrategy.process(order);

        assertThat(result).isNotNull();
        assertThat(result.getTransactionId()).isNotNull();
        assertThat(result.getTransactionId()).startsWith("PP-");
        assertThat(result.getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Should return failure for amount above maximum")
    void shouldReturnFailureForAmountAboveMaximum() {
        Product product = new Product("SKU-001", "Product", Money.of(600000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        PaymentResult result = paymentStrategy.process(order);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo("AMOUNT_EXCEEDED");
    }

    @Test
    @DisplayName("Should check if order can be processed")
    void shouldCheckIfOrderCanBeProcessed() {
        Product validProduct = new Product("SKU-001", "Product", Money.of(10000));
        Order validOrder = new Order.Builder()
            .addItem(validProduct, 1)
            .build();

        Product invalidProduct = new Product("SKU-002", "Product", Money.of(600000));
        Order invalidOrder = new Order.Builder()
            .addItem(invalidProduct, 1)
            .build();

        assertThat(paymentStrategy.canProcess(validOrder)).isTrue();
        assertThat(paymentStrategy.canProcess(invalidOrder)).isFalse();
    }

    @Test
    @DisplayName("Should return correct payment method name")
    void shouldReturnCorrectPaymentMethodName() {
        assertThat(paymentStrategy.getPaymentMethodName()).isEqualTo("PAYPAY");
    }

    @Test
    @DisplayName("Should return correct processing fee rate")
    void shouldReturnCorrectProcessingFeeRate() {
        assertThat(paymentStrategy.getProcessingFeeRate()).isEqualTo(0.02);
    }
}
