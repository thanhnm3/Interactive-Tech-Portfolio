package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Money;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Product entity
 */
@DisplayName("Product Entity Tests")
class ProductTest {

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
    @DisplayName("Should throw exception for null SKU")
    void shouldThrowExceptionForNullSku() {
        assertThatThrownBy(() -> new Product(null, "Product", Money.of(1000)))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("SKU cannot be null");
    }

    @Test
    @DisplayName("Should throw exception for null name")
    void shouldThrowExceptionForNullName() {
        assertThatThrownBy(() -> new Product("SKU-001", null, Money.of(1000)))
            .isInstanceOf(NullPointerException.class)
            .hasMessageContaining("Name cannot be null");
    }

    @Test
    @DisplayName("Should update product details")
    void shouldUpdateProductDetails() {
        Product product = new Product("SKU-001", "Old Name", Money.of(1000));
        product.updateDetails("New Name", "New Description");

        assertThat(product.getName()).isEqualTo("New Name");
        assertThat(product.getDescription()).isEqualTo("New Description");
        assertThat(product.getUpdatedAt()).isNotNull();
    }

    @Test
    @DisplayName("Should update price and set original price")
    void shouldUpdatePriceAndSetOriginalPrice() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        product.updatePrice(Money.of(800));

        assertThat(product.getPrice().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(800));
        assertThat(product.getOriginalPrice().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
    }

    @Test
    @DisplayName("Should set sale price")
    void shouldSetSalePrice() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        product.setSalePrice(Money.of(700));

        assertThat(product.getPrice().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(700));
        assertThat(product.getOriginalPrice().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(product.isOnSale()).isTrue();
    }

    @Test
    @DisplayName("Should remove sale and restore original price")
    void shouldRemoveSaleAndRestoreOriginalPrice() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        product.setSalePrice(Money.of(700));
        product.removeSale();

        assertThat(product.getPrice().getAmount()).isEqualByComparingTo(BigDecimal.valueOf(1000));
        assertThat(product.getOriginalPrice()).isNull();
        assertThat(product.isOnSale()).isFalse();
    }

    @Test
    @DisplayName("Should calculate discount percentage")
    void shouldCalculateDiscountPercentage() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        product.setSalePrice(Money.of(800));

        assertThat(product.getDiscountPercentage()).isEqualTo(20.0);
    }

    @Test
    @DisplayName("Should add stock quantity")
    void shouldAddStockQuantity() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        product.addStock(50);

        assertThat(product.getStockQuantity()).isEqualTo(50);
    }

    @Test
    @DisplayName("Should throw exception when adding negative stock")
    void shouldThrowExceptionWhenAddingNegativeStock() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));

        assertThatThrownBy(() -> product.addStock(-10))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Quantity must be positive");
    }

    @Test
    @DisplayName("Should deduct stock quantity")
    void shouldDeductStockQuantity() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        product.addStock(50);
        boolean success = product.deductStock(20);

        assertThat(success).isTrue();
        assertThat(product.getStockQuantity()).isEqualTo(30);
    }

    @Test
    @DisplayName("Should return false when deducting more than available stock")
    void shouldReturnFalseWhenDeductingMoreThanAvailableStock() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        product.addStock(10);
        boolean success = product.deductStock(20);

        assertThat(success).isFalse();
        assertThat(product.getStockQuantity()).isEqualTo(10);
    }

    @Test
    @DisplayName("Should check if product is on sale")
    void shouldCheckIfProductIsOnSale() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        assertThat(product.isOnSale()).isFalse();

        product.setSalePrice(Money.of(800));
        assertThat(product.isOnSale()).isTrue();
    }

    @Test
    @DisplayName("Should check if stock is low")
    void shouldCheckIfStockIsLow() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        product.setMinStockLevel(10);
        product.addStock(5);

        assertThat(product.isLowStock()).isTrue();
    }

    @Test
    @DisplayName("Should check if product is in stock")
    void shouldCheckIfProductIsInStock() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        assertThat(product.isInStock()).isFalse();

        product.addStock(10);
        assertThat(product.isInStock()).isTrue();
    }

    @Test
    @DisplayName("Should activate and deactivate product")
    void shouldActivateAndDeactivateProduct() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        product.deactivate();

        assertThat(product.isActive()).isFalse();

        product.activate();
        assertThat(product.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should mark and unmark as featured")
    void shouldMarkAndUnmarkAsFeatured() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        product.markAsFeatured();

        assertThat(product.isFeatured()).isTrue();

        product.unmarkAsFeatured();
        assertThat(product.isFeatured()).isFalse();
    }

    @Test
    @DisplayName("Should set category")
    void shouldSetCategory() {
        Product product = new Product("SKU-001", "Product", Money.of(1000));
        Category category = new Category("Electronics", "Electronic products");

        product.setCategory(category);

        assertThat(product.getCategory()).isEqualTo(category);
    }

    @Test
    @DisplayName("Should have correct equals and hashCode")
    void shouldHaveCorrectEqualsAndHashCode() {
        Product product1 = new Product("SKU-001", "Product", Money.of(1000));
        Product product2 = new Product("SKU-002", "Product", Money.of(1000));

        assertThat(product1).isNotEqualTo(product2);
        assertThat(product1.hashCode()).isNotEqualTo(product2.hashCode());
    }
}
