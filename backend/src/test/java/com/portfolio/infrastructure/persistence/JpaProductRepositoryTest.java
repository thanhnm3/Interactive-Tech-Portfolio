package com.portfolio.infrastructure.persistence;

import com.portfolio.domain.entity.Category;
import com.portfolio.domain.entity.Product;
import com.portfolio.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple unit tests for JpaProductRepository
 * Tests basic product entity without database
 */
@DisplayName("JpaProductRepository Tests")
class JpaProductRepositoryTest {

    @Test
    @DisplayName("Should create product with required fields")
    void shouldCreateProductWithRequiredFields() {
        Product product = new Product("SKU-001", "Test Product", Money.of(1000));

        assertThat(product.getSku()).isEqualTo("SKU-001");
        assertThat(product.getName()).isEqualTo("Test Product");
        assertThat(product.getPrice().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(product.getStockQuantity()).isEqualTo(0);
        assertThat(product.isActive()).isTrue();
        assertThat(product.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should add stock to product")
    void shouldAddStockToProduct() {
        Product product = new Product("SKU-002", "Product", Money.of(500));
        product.addStock(50);

        assertThat(product.getStockQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("Should set category for product")
    void shouldSetCategoryForProduct() {
        Category category = new Category("Electronics", "Electronic products");
        Product product = new Product("SKU-003", "Product", Money.of(1000));
        product.setCategory(category);

        assertThat(product.getCategory()).isNotNull();
        assertThat(product.getCategory().getName()).isEqualTo("Electronics");
    }

    @Test
    @DisplayName("Should verify Money value object")
    void shouldVerifyMoneyValueObject() {
        Money money = Money.of(1500);

        assertThat(money.getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1500));
    }
}
