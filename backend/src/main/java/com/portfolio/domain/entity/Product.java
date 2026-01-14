package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Money;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Product entity representing sellable items
 * Contains pricing, inventory, and category information
 */
@Entity
@Table(name = "products")
public class Product {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private String id;

    @Column(name = "sku", nullable = false, unique = true)
    private String sku;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "price", nullable = false, precision = 12, scale = 2)
    private BigDecimal price;

    @Column(name = "original_price", precision = 12, scale = 2)
    private BigDecimal originalPrice;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @Column(name = "stock_quantity", nullable = false)
    private Integer stockQuantity;

    @Column(name = "min_stock_level")
    private Integer minStockLevel;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "is_featured")
    private boolean isFeatured;

    @Column(name = "weight")
    private Double weight;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Default constructor for JPA
     */
    protected Product() {
    }

    /**
     * Create product with required fields
     * @param sku - stock keeping unit
     * @param name - product name
     * @param price - product price
     */
    public Product(String sku, String name, Money price) {
        this.id = UUID.randomUUID().toString();
        this.sku = Objects.requireNonNull(sku, "SKU cannot be null");
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.price = price.getAmount();
        this.stockQuantity = 0;
        this.minStockLevel = 10;
        this.isActive = true;
        this.isFeatured = false;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Get product ID
     * @return String - UUID string
     */
    public String getId() {
        return id;
    }

    /**
     * Get SKU
     * @return String - stock keeping unit
     */
    public String getSku() {
        return sku;
    }

    /**
     * Get product name
     * @return String - name
     */
    public String getName() {
        return name;
    }

    /**
     * Get description
     * @return String - description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get price as Money value object
     * @return Money - current price
     */
    public Money getPrice() {
        return Money.of(price);
    }

    /**
     * Get original price as Money value object
     * @return Money - original price or null
     */
    public Money getOriginalPrice() {
        return originalPrice != null ? Money.of(originalPrice) : null;
    }

    /**
     * Get category
     * @return Category - product category
     */
    public Category getCategory() {
        return category;
    }

    /**
     * Get stock quantity
     * @return Integer - available stock
     */
    public Integer getStockQuantity() {
        return stockQuantity;
    }

    /**
     * Get minimum stock level
     * @return Integer - minimum stock threshold
     */
    public Integer getMinStockLevel() {
        return minStockLevel;
    }

    /**
     * Get image URL
     * @return String - image URL
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * Check if product is active
     * @return boolean - active status
     */
    public boolean isActive() {
        return isActive;
    }

    /**
     * Check if product is featured
     * @return boolean - featured status
     */
    public boolean isFeatured() {
        return isFeatured;
    }

    /**
     * Get product weight
     * @return Double - weight in kg
     */
    public Double getWeight() {
        return weight;
    }

    /**
     * Get creation timestamp
     * @return LocalDateTime - created at
     */
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    /**
     * Get last update timestamp
     * @return LocalDateTime - updated at
     */
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    /**
     * Check if product is on sale
     * @return boolean - true if discounted
     */
    public boolean isOnSale() {
        return originalPrice != null && price.compareTo(originalPrice) < 0;
    }

    /**
     * Get discount percentage
     * @return double - discount percentage (0-100)
     */
    public double getDiscountPercentage() {
        if (!isOnSale()) {
            return 0.0;
        }
        return originalPrice.subtract(price)
            .divide(originalPrice, 4, java.math.RoundingMode.HALF_UP)
            .multiply(BigDecimal.valueOf(100))
            .doubleValue();
    }

    /**
     * Check if stock is low
     * @return boolean - true if below minimum
     */
    public boolean isLowStock() {
        return stockQuantity <= minStockLevel;
    }

    /**
     * Check if product is in stock
     * @return boolean - true if available
     */
    public boolean isInStock() {
        return stockQuantity > 0;
    }

    /**
     * Update product details
     * @param name - new name
     * @param description - new description
     */
    public void updateDetails(String name, String description) {
        this.name = name;
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set description
     * @param description - product description
     */
    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update price
     * @param newPrice - new price
     */
    public void updatePrice(Money newPrice) {
        this.originalPrice = this.price;
        this.price = newPrice.getAmount();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set sale price
     * @param salePrice - discounted price
     */
    public void setSalePrice(Money salePrice) {
        if (this.originalPrice == null) {
            this.originalPrice = this.price;
        }
        this.price = salePrice.getAmount();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Remove sale and restore original price
     */
    public void removeSale() {
        if (this.originalPrice != null) {
            this.price = this.originalPrice;
            this.originalPrice = null;
            this.updatedAt = LocalDateTime.now();
        }
    }

    /**
     * Set category
     * @param category - product category
     */
    public void setCategory(Category category) {
        this.category = category;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Add stock quantity
     * @param quantity - quantity to add
     */
    public void addStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.stockQuantity += quantity;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deduct stock quantity
     * @param quantity - quantity to deduct
     * @return boolean - true if successful
     */
    public boolean deductStock(int quantity) {
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        if (this.stockQuantity >= quantity) {
            this.stockQuantity -= quantity;
            this.updatedAt = LocalDateTime.now();
            return true;
        }
        return false;
    }

    /**
     * Set minimum stock level
     * @param minStockLevel - minimum threshold
     */
    public void setMinStockLevel(Integer minStockLevel) {
        this.minStockLevel = minStockLevel;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set image URL
     * @param imageUrl - image URL
     */
    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set weight
     * @param weight - weight in kg
     */
    public void setWeight(Double weight) {
        this.weight = weight;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Activate product
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deactivate product
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark as featured
     */
    public void markAsFeatured() {
        this.isFeatured = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Unmark as featured
     */
    public void unmarkAsFeatured() {
        this.isFeatured = false;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return Objects.equals(id, product.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Product[id=%s, sku=%s, name=%s, price=%s]",
            id, sku, name, price);
    }
}
