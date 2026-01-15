package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for OrderItem entity
 */
@DisplayName("OrderItem Entity Tests")
class OrderItemTest {

    @Test
    @DisplayName("Should create order item with product and quantity")
    void shouldCreateOrderItemWithProductAndQuantity() {
        Product product = new Product("SKU-001", "Test Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        OrderItem item = order.getItems().get(0);

        assertThat(item.getProduct()).isEqualTo(product);
        assertThat(item.getQuantity()).isEqualTo(1);
        assertThat(item.getProductName()).isEqualTo("Test Product");
        assertThat(item.getProductSku()).isEqualTo("SKU-001");
        assertThat(item.getUnitPrice().getAmount().doubleValue()).isEqualTo(1000.0);
    }

    @Test
    @DisplayName("Should calculate line total correctly")
    void shouldCalculateLineTotalCorrectly() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 3)
            .build();

        OrderItem item = order.getItems().get(0);

        assertThat(item.getLineTotal().getAmount().doubleValue()).isEqualTo(3000.0);
    }

    @Test
    @DisplayName("Should throw exception for zero quantity")
    void shouldThrowExceptionForZeroQuantity() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        OrderItem item = order.getItems().get(0);

        assertThatThrownBy(() -> item.updateQuantity(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Quantity must be positive");
    }

    @Test
    @DisplayName("Should update quantity and recalculate total")
    void shouldUpdateQuantityAndRecalculateTotal() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 2)
            .build();

        OrderItem item = order.getItems().get(0);
        item.updateQuantity(5);

        assertThat(item.getQuantity()).isEqualTo(5);
        assertThat(item.getLineTotal().getAmount().doubleValue()).isEqualTo(5000.0);
    }

    @Test
    @DisplayName("Should apply discount to order item")
    void shouldApplyDiscountToOrderItem() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 2)
            .build();

        OrderItem item = order.getItems().get(0);
        Money originalTotal = item.getLineTotal();
        item.applyDiscount(Money.of(200));

        assertThat(item.getDiscountAmount().getAmount().doubleValue()).isEqualTo(200.0);
        assertThat(item.getLineTotal().getAmount().doubleValue())
            .isLessThan(originalTotal.getAmount().doubleValue());
    }

    @Test
    @DisplayName("Should remove discount from order item")
    void shouldRemoveDiscountFromOrderItem() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 2)
            .build();

        OrderItem item = order.getItems().get(0);
        Money originalTotal = item.getLineTotal();
        item.applyDiscount(Money.of(200));
        item.removeDiscount();

        assertThat(item.getDiscountAmount().getAmount().doubleValue()).isEqualTo(0.0);
        assertThat(item.getLineTotal()).isEqualTo(originalTotal);
    }

    @Test
    @DisplayName("Should snapshot product name and SKU at creation")
    void shouldSnapshotProductNameAndSkuAtCreation() {
        Product product = new Product("SKU-001", "Original Name", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        OrderItem item = order.getItems().get(0);
        product.updateDetails("Updated Name", "Description");

        assertThat(item.getProductName()).isEqualTo("Original Name");
        assertThat(item.getProductSku()).isEqualTo("SKU-001");
    }
}
