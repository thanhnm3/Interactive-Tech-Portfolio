package com.portfolio.domain.strategy;

import com.portfolio.domain.entity.Order;
import com.portfolio.domain.entity.Product;
import com.portfolio.domain.valueobject.Money;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for PaymentContext
 */
@DisplayName("PaymentContext Tests")
class PaymentContextTest {

    private PaymentContext paymentContext;
    private CreditCardPayment creditCardPayment;
    private PayPayPayment payPayPayment;
    private BankTransferPayment bankTransferPayment;
    private Order testOrder;

    @BeforeEach
    void setUp() {
        creditCardPayment = new CreditCardPayment();
        payPayPayment = new PayPayPayment();
        bankTransferPayment = new BankTransferPayment();
        paymentContext = new PaymentContext(creditCardPayment, payPayPayment, bankTransferPayment);

        Product product = new Product("SKU-001", "Test Product", Money.of(5000));
        testOrder = new Order.Builder()
            .addItem(product, 1)
            .withTaxRate(0.10)
            .build();
    }

    @Test
    @DisplayName("Should set payment strategy by method name")
    void shouldSetPaymentStrategyByMethodName() {
        paymentContext.setStrategy("CREDIT_CARD");
        assertThat(paymentContext.getCurrentStrategyName()).isEqualTo("CREDIT_CARD");

        paymentContext.setStrategy("PAYPAY");
        assertThat(paymentContext.getCurrentStrategyName()).isEqualTo("PAYPAY");
    }

    @Test
    @DisplayName("Should throw exception for unknown payment method")
    void shouldThrowExceptionForUnknownPaymentMethod() {
        assertThatThrownBy(() -> paymentContext.setStrategy("UNKNOWN"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Unknown payment method");
    }

    @Test
    @DisplayName("Should execute payment with current strategy")
    void shouldExecutePaymentWithCurrentStrategy() {
        paymentContext.setStrategy("CREDIT_CARD");
        PaymentResult result = paymentContext.executePayment(testOrder);

        assertThat(result).isNotNull();
        assertThat(result.getPaymentMethod()).isEqualTo("CREDIT_CARD");
    }

    @Test
    @DisplayName("Should execute payment with specified method")
    void shouldExecutePaymentWithSpecifiedMethod() {
        PaymentResult result = paymentContext.executePayment(testOrder, "PAYPAY");

        assertThat(result).isNotNull();
        assertThat(result.getPaymentMethod()).isEqualTo("PAYPAY");
    }

    @Test
    @DisplayName("Should get available payment methods")
    void shouldGetAvailablePaymentMethods() {
        var methods = paymentContext.getAvailablePaymentMethods();

        assertThat(methods).contains("CREDIT_CARD", "PAYPAY", "BANK_TRANSFER");
    }

    @Test
    @DisplayName("Should get payment method details")
    void shouldGetPaymentMethodDetails() {
        var details = paymentContext.getPaymentMethodDetails();

        assertThat(details).hasSize(3);
        assertThat(details).anyMatch(d -> d.get("name").equals("CREDIT_CARD"));
    }

    @Test
    @DisplayName("Should get available methods for order")
    void shouldGetAvailableMethodsForOrder() {
        var methods = paymentContext.getAvailableMethodsForOrder(testOrder);

        assertThat(methods).isNotEmpty();
    }

    @Test
    @DisplayName("Should return failure when no strategy is set")
    void shouldReturnFailureWhenNoStrategyIsSet() {
        PaymentContext emptyContext = new PaymentContext(
            new CreditCardPayment(),
            new PayPayPayment(),
            new BankTransferPayment()
        );
        emptyContext.setStrategy((PaymentStrategy) null);

        PaymentResult result = emptyContext.executePayment(testOrder);

        assertThat(result.isSuccess()).isFalse();
        assertThat(result.getStatus()).isEqualTo("FAILED");
    }

    @Test
    @DisplayName("Should get strategy by name")
    void shouldGetStrategyByName() {
        PaymentStrategy strategy = paymentContext.getStrategy("CREDIT_CARD");

        assertThat(strategy).isNotNull();
        assertThat(strategy).isInstanceOf(CreditCardPayment.class);
    }
}
