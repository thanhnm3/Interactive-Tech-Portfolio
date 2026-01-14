package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Money;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.util.Objects;
import java.util.UUID;

/**
 * Order item entity representing a product line in an order
 * Contains quantity, pricing, and product reference
 */
@Entity
@Table(name = "order_items")
public class OrderItem {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id", nullable = false)
    private Order order;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(name = "product_name", nullable = false)
    private String productName;

    @Column(name = "product_sku", nullable = false)
    private String productSku;

    @Column(name = "quantity", nullable = false)
    private Integer quantity;

    @Column(name = "unit_price", nullable = false, precision = 12, scale = 2)
    private BigDecimal unitPrice;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "line_total", nullable = false, precision = 12, scale = 2)
    private BigDecimal lineTotal;

    /**
     * Default constructor for JPA
     */
    protected OrderItem() {
    }

    /**
     * Create order item
     * @param order - parent order
     * @param product - product being ordered
     * @param quantity - quantity ordered
     */
    public OrderItem(Order order, Product product, int quantity) {
        this.id = UUID.randomUUID().toString();
        this.order = Objects.requireNonNull(order, "Order cannot be null");
        this.product = Objects.requireNonNull(product, "Product cannot be null");
        this.productName = product.getName();
        this.productSku = product.getSku();
        this.quantity = validateQuantity(quantity);
        this.unitPrice = product.getPrice().getAmount();
        this.discountAmount = BigDecimal.ZERO;
        calculateLineTotal();
    }

    /**
     * Validate quantity is positive
     * @param quantity - quantity to validate
     * @return int - validated quantity
     */
    private int validateQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        return quantity;
    }

    /**
     * Calculate line total from quantity, price, and discount
     */
    private void calculateLineTotal() {
        BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
        this.lineTotal = subtotal.subtract(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }

    /**
     * Get item ID
     * @return String - UUID string
     */
    public String getId() {
        return id;
    }

    /**
     * Get parent order
     * @return Order - parent order
     */
    public Order getOrder() {
        return order;
    }

    /**
     * Get product
     * @return Product - ordered product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * Get product name (snapshot at order time)
     * @return String - product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Get product SKU (snapshot at order time)
     * @return String - product SKU
     */
    public String getProductSku() {
        return productSku;
    }

    /**
     * Get quantity
     * @return Integer - quantity ordered
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Get unit price as Money
     * @return Money - price per unit
     */
    public Money getUnitPrice() {
        return Money.of(unitPrice);
    }

    /**
     * Get discount amount as Money
     * @return Money - discount applied
     */
    public Money getDiscountAmount() {
        return Money.of(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }

    /**
     * Get line total as Money
     * @return Money - total for this line
     */
    public Money getLineTotal() {
        return Money.of(lineTotal);
    }

    /**
     * Update quantity
     * @param newQuantity - new quantity
     */
    public void updateQuantity(int newQuantity) {
        this.quantity = validateQuantity(newQuantity);
        calculateLineTotal();
    }

    /**
     * Apply discount to this item
     * @param discount - discount amount
     */
    public void applyDiscount(Money discount) {
        this.discountAmount = discount.getAmount();
        calculateLineTotal();
    }

    /**
     * Remove discount from this item
     */
    public void removeDiscount() {
        this.discountAmount = BigDecimal.ZERO;
        calculateLineTotal();
    }

    /**
     * Set order reference (used by Order.Builder)
     * @param order - parent order
     */
    void setOrder(Order order) {
        this.order = order;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(id, orderItem.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("OrderItem[id=%s, product=%s, quantity=%d, total=%s]",
            id, productName, quantity, lineTotal);
    }
}
