package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Money;
import com.portfolio.domain.valueobject.OrderId;
import com.portfolio.domain.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Order entity
 */
@DisplayName("Order Entity Tests")
class OrderTest {

    @Test
    @DisplayName("Should create order using Builder pattern")
    void shouldCreateOrderUsingBuilder() {
        Product product = new Product("SKU-001", "Test Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 2)
            .withTaxRate(0.10)
            .build();

        assertThat(order.getId()).isNotNull();
        assertThat(order.getOrderNumber()).isNotNull();
        assertThat(order.getItems()).hasSize(1);
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        assertThat(order.getTotalAmount().getAmount().doubleValue()).isGreaterThan(0);
    }

    @Test
    @DisplayName("Should throw exception when building order without items")
    void shouldThrowExceptionWhenBuildingOrderWithoutItems() {
        assertThatThrownBy(() -> new Order.Builder().build())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Order must have at least one item");
    }

    @Test
    @DisplayName("Should calculate order totals correctly")
    void shouldCalculateOrderTotalsCorrectly() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 2)
            .shippingAmount(Money.of(500))
            .applyDiscount(Money.of(100))
            .withTaxRate(0.10)
            .build();

        assertThat(order.getSubtotal().getAmount().doubleValue()).isEqualTo(2000.0);
        assertThat(order.getTaxAmount().getAmount().doubleValue()).isEqualTo(200.0);
        assertThat(order.getShippingAmount().getAmount().doubleValue()).isEqualTo(500.0);
        assertThat(order.getDiscountAmount().getAmount().doubleValue()).isEqualTo(100.0);
    }

    @Test
    @DisplayName("Should transition order status correctly")
    void shouldTransitionOrderStatusCorrectly() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        order.confirm();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);

        order.startProcessing();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PROCESSING);

        order.ship();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.SHIPPED);

        order.deliver();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.DELIVERED);
        assertThat(order.getCompletedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should throw exception for invalid status transition")
    void shouldThrowExceptionForInvalidStatusTransition() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        assertThatThrownBy(() -> order.startProcessing())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot transition");
    }

    @Test
    @DisplayName("Should cancel order when in cancellable status")
    void shouldCancelOrderWhenInCancellableStatus() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        order.confirm();
        order.cancel();

        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.CANCELLED);
    }

    @Test
    @DisplayName("Should throw exception when cancelling non-cancellable order")
    void shouldThrowExceptionWhenCancellingNonCancellableOrder() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        order.confirm();
        order.startProcessing();
        order.ship();

        assertThatThrownBy(() -> order.cancel())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Cannot cancel order");
    }

    @Test
    @DisplayName("Should refund delivered order")
    void shouldRefundDeliveredOrder() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        order.confirm();
        order.startProcessing();
        order.ship();
        order.deliver();
        order.refund();

        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.REFUNDED);
    }

    @Test
    @DisplayName("Should throw exception when refunding non-delivered order")
    void shouldThrowExceptionWhenRefundingNonDeliveredOrder() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        assertThatThrownBy(() -> order.refund())
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Can only refund delivered orders");
    }

    @Test
    @DisplayName("Should check if order is cancellable")
    void shouldCheckIfOrderIsCancellable() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        assertThat(order.isCancellable()).isTrue();

        order.confirm();
        assertThat(order.isCancellable()).isTrue();

        order.startProcessing();
        assertThat(order.isCancellable()).isFalse();
    }

    @Test
    @DisplayName("Should check if order is completed")
    void shouldCheckIfOrderIsCompleted() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        assertThat(order.isCompleted()).isFalse();

        order.confirm();
        order.startProcessing();
        order.ship();
        order.deliver();

        assertThat(order.isCompleted()).isTrue();
    }

    @Test
    @DisplayName("Should calculate total item count")
    void shouldCalculateTotalItemCount() {
        Product product1 = new Product("SKU-001", "Product 1", Money.of(1000));
        Product product2 = new Product("SKU-002", "Product 2", Money.of(500));
        Order order = new Order.Builder()
            .addItem(product1, 2)
            .addItem(product2, 3)
            .build();

        assertThat(order.getTotalItemCount()).isEqualTo(5);
    }

    @Test
    @DisplayName("Should update order notes")
    void shouldUpdateOrderNotes() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .notes("Initial note")
            .build();

        order.updateNotes("Updated note");

        assertThat(order.getNotes()).isEqualTo("Updated note");
    }

    @Test
    @DisplayName("Should set user ID in builder")
    void shouldSetUserIdInBuilder() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        UserId userId = UserId.generate();
        Order order = new Order.Builder()
            .userId(userId)
            .addItem(product, 1)
            .build();

        assertThat(order.getUserId()).isEqualTo(userId);
    }
}
