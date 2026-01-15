package com.portfolio.domain.strategy;

import com.portfolio.domain.entity.Order;
import com.portfolio.domain.entity.Product;
import com.portfolio.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for BankTransferPayment strategy
 */
@DisplayName("BankTransferPayment Strategy Tests")
class BankTransferPaymentTest {

    private final BankTransferPayment paymentStrategy = new BankTransferPayment();

    @Test
    @DisplayName("Should process valid bank transfer payment")
    void shouldProcessValidBankTransferPayment() {
        Product product = new Product("SKU-001", "Product", Money.of(5000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .withTaxRate(0.10)
            .build();

        PaymentResult result = paymentStrategy.process(order);

        assertThat(result).isNotNull();
        assertThat(result.getTransactionId()).isNotNull();
        assertThat(result.getTransactionId()).startsWith("BT-");
        assertThat(result.getStatus()).isEqualTo("PENDING");
    }

    @Test
    @DisplayName("Should return failure for amount below minimum")
    void shouldReturnFailureForAmountBelowMinimum() {
        Product product = new Product("SKU-001", "Product", Money.of(500));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        PaymentResult result = paymentStrategy.process(order);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getErrorCode()).isEqualTo("MIN_AMOUNT_NOT_MET");
    }

    @Test
    @DisplayName("Should check if order can be processed")
    void shouldCheckIfOrderCanBeProcessed() {
        Product validProduct = new Product("SKU-001", "Product", Money.of(5000));
        Order validOrder = new Order.Builder()
            .addItem(validProduct, 1)
            .build();

        Product invalidProduct = new Product("SKU-002", "Product", Money.of(500));
        Order invalidOrder = new Order.Builder()
            .addItem(invalidProduct, 1)
            .build();

        assertThat(paymentStrategy.canProcess(validOrder)).isTrue();
        assertThat(paymentStrategy.canProcess(invalidOrder)).isFalse();
    }

    @Test
    @DisplayName("Should return correct payment method name")
    void shouldReturnCorrectPaymentMethodName() {
        assertThat(paymentStrategy.getPaymentMethodName()).isEqualTo("BANK_TRANSFER");
    }

    @Test
    @DisplayName("Should return zero processing fee rate")
    void shouldReturnZeroProcessingFeeRate() {
        assertThat(paymentStrategy.getProcessingFeeRate()).isEqualTo(0.0);
    }

    @Test
    @DisplayName("Should get fixed fee")
    void shouldGetFixedFee() {
        Money fixedFee = paymentStrategy.getFixedFee();

        assertThat(fixedFee.getAmount().doubleValue()).isEqualTo(330.0);
    }

    @Test
    @DisplayName("Should generate transfer instructions")
    void shouldGenerateTransferInstructions() {
        Product product = new Product("SKU-001", "Product", Money.of(5000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        String instructions = paymentStrategy.generateTransferInstructions(order);

        assertThat(instructions).contains("Bank Transfer Instructions");
        assertThat(instructions).contains(order.getOrderNumber());
        assertThat(instructions).contains(order.getTotalAmount().toString());
    }
}
