package com.portfolio.infrastructure.persistence;

import com.portfolio.domain.entity.Order;
import com.portfolio.domain.entity.Product;
import com.portfolio.domain.valueobject.Money;
import com.portfolio.domain.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple unit tests for JpaOrderRepository
 * Tests basic order entity without database
 */
@DisplayName("JpaOrderRepository Tests")
class JpaOrderRepositoryTest {

    @Test
    @DisplayName("Should create order using Builder")
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
    }

    @Test
    @DisplayName("Should create order with user ID")
    void shouldCreateOrderWithUserId() {
        Product product = new Product("SKU-002", "Product", Money.of(500));
        UserId userId = UserId.generate();
        
        Order order = new Order.Builder()
            .userId(userId)
            .addItem(product, 1)
            .build();

        assertThat(order.getUserId()).isEqualTo(userId);
        assertThat(order.getItems()).hasSize(1);
    }

    @Test
    @DisplayName("Should verify Order status")
    void shouldVerifyOrderStatus() {
        Product product = new Product("SKU-003", "Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        
        order.confirm();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);
    }

    @Test
    @DisplayName("Should verify Product creation")
    void shouldVerifyProductCreation() {
        Product product = new Product("SKU-004", "New Product", Money.of(2000));

        assertThat(product.getSku()).isEqualTo("SKU-004");
        assertThat(product.getName()).isEqualTo("New Product");
        assertThat(product.getPrice().getAmount().doubleValue()).isEqualTo(2000.0);
    }
}
