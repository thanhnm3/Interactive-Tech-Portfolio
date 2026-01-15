package com.portfolio;

import com.portfolio.domain.entity.Order;
import com.portfolio.domain.entity.Product;
import com.portfolio.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple application tests
 * Tests basic domain logic without database
 */
@DisplayName("Application Tests")
class ApplicationIntegrationTest {

    @Test
    @DisplayName("Should create product")
    void shouldCreateProduct() {
        Product product = new Product("SKU-001", "Test Product", Money.of(5000));
        product.addStock(100);

        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getStockQuantity()).isEqualTo(100);
        assertThat(product.getPrice().getAmount().doubleValue()).isEqualTo(5000.0);
    }

    @Test
    @DisplayName("Should create order with products")
    void shouldCreateOrderWithProducts() {
        Product product1 = new Product("SKU-002", "Product 1", Money.of(1000));
        Product product2 = new Product("SKU-003", "Product 2", Money.of(2000));

        Order order = new Order.Builder()
            .addItem(product1, 2)
            .addItem(product2, 1)
            .withTaxRate(0.10)
            .build();

        assertThat(order.getItems()).hasSize(2);
        assertThat(order.getTotalItemCount()).isEqualTo(3);
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
    }

    @Test
    @DisplayName("Should update product price")
    void shouldUpdateProductPrice() {
        Product product = new Product("SKU-004", "Product", Money.of(1000));
        product.updatePrice(Money.of(1500));

        assertThat(product.getPrice().getAmount().doubleValue()).isEqualTo(1500.0);
    }

    @Test
    @DisplayName("Should handle order status transitions")
    void shouldHandleOrderStatusTransitions() {
        Product product = new Product("SKU-005", "Test Product", Money.of(1000));
        Order order = new Order.Builder()
            .addItem(product, 1)
            .build();

        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PENDING);
        
        order.confirm();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.CONFIRMED);
        
        order.startProcessing();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.PROCESSING);
        
        order.ship();
        assertThat(order.getStatus()).isEqualTo(Order.OrderStatus.SHIPPED);
    }
}
