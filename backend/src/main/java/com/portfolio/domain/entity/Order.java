package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Money;
import com.portfolio.domain.valueobject.OrderId;
import com.portfolio.domain.valueobject.UserId;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Order entity using Builder Pattern for complex construction
 * Contains order items, pricing, and status management
 */
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private String id;

    @Column(name = "order_number", nullable = false, unique = true)
    private String orderNumber;

    @Column(name = "user_id", columnDefinition = "uuid")
    private String userId;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<OrderItem> items = new ArrayList<>();

    @Column(name = "subtotal", nullable = false, precision = 12, scale = 2)
    private BigDecimal subtotal;

    @Column(name = "tax_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal taxAmount;

    @Column(name = "shipping_amount", precision = 12, scale = 2)
    private BigDecimal shippingAmount;

    @Column(name = "discount_amount", precision = 12, scale = 2)
    private BigDecimal discountAmount;

    @Column(name = "total_amount", nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private OrderStatus status;

    @Column(name = "shipping_address", columnDefinition = "TEXT")
    private String shippingAddress;

    @Column(name = "billing_address", columnDefinition = "TEXT")
    private String billingAddress;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    /**
     * Order status enumeration
     */
    public enum OrderStatus {
        PENDING,
        CONFIRMED,
        PROCESSING,
        SHIPPED,
        DELIVERED,
        CANCELLED,
        REFUNDED
    }

    /**
     * Default constructor for JPA
     */
    protected Order() {
    }

    /**
     * Private constructor for Builder pattern
     * @param builder - Order.Builder instance
     */
    private Order(Builder builder) {
        this.id = builder.id.getValue().toString();
        this.orderNumber = builder.orderNumber;
        this.userId = builder.userId != null ? builder.userId.getValue().toString() : null;
        this.items = builder.items;
        this.subtotal = builder.subtotal;
        this.taxAmount = builder.taxAmount;
        this.shippingAmount = builder.shippingAmount;
        this.discountAmount = builder.discountAmount;
        this.totalAmount = builder.totalAmount;
        this.status = builder.status;
        this.shippingAddress = builder.shippingAddress;
        this.billingAddress = builder.billingAddress;
        this.notes = builder.notes;
        this.createdAt = LocalDateTime.now();

        // Link items to this order
        for (OrderItem item : this.items) {
            item.setOrder(this);
        }
    }

    /**
     * Get order ID as value object
     * @return OrderId - order identifier
     */
    public OrderId getId() {
        return OrderId.of(id);
    }

    /**
     * Get order number
     * @return String - human-readable order number
     */
    public String getOrderNumber() {
        return orderNumber;
    }

    /**
     * Get user ID as value object
     * @return UserId - user identifier or null for guest
     */
    public UserId getUserId() {
        return userId != null ? UserId.of(userId) : null;
    }

    /**
     * Get order items (immutable view)
     * @return List of order items
     */
    public List<OrderItem> getItems() {
        return Collections.unmodifiableList(items);
    }

    /**
     * Get subtotal as Money
     * @return Money - subtotal before tax and shipping
     */
    public Money getSubtotal() {
        return Money.of(subtotal);
    }

    /**
     * Get tax amount as Money
     * @return Money - tax amount
     */
    public Money getTaxAmount() {
        return Money.of(taxAmount);
    }

    /**
     * Get shipping amount as Money
     * @return Money - shipping cost
     */
    public Money getShippingAmount() {
        return Money.of(shippingAmount != null ? shippingAmount : BigDecimal.ZERO);
    }

    /**
     * Get discount amount as Money
     * @return Money - total discount
     */
    public Money getDiscountAmount() {
        return Money.of(discountAmount != null ? discountAmount : BigDecimal.ZERO);
    }

    /**
     * Get total amount as Money
     * @return Money - final total
     */
    public Money getTotalAmount() {
        return Money.of(totalAmount);
    }

    /**
     * Get order status
     * @return OrderStatus - current status
     */
    public OrderStatus getStatus() {
        return status;
    }

    /**
     * Get shipping address
     * @return String - shipping address
     */
    public String getShippingAddress() {
        return shippingAddress;
    }

    /**
     * Get billing address
     * @return String - billing address
     */
    public String getBillingAddress() {
        return billingAddress;
    }

    /**
     * Get notes
     * @return String - order notes
     */
    public String getNotes() {
        return notes;
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
     * Get completion timestamp
     * @return LocalDateTime - completed at
     */
    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    /**
     * Get total number of items
     * @return int - total quantity
     */
    public int getTotalItemCount() {
        return items.stream().mapToInt(OrderItem::getQuantity).sum();
    }

    /**
     * Check if order can be cancelled
     * @return boolean - true if cancellable
     */
    public boolean isCancellable() {
        return status == OrderStatus.PENDING || status == OrderStatus.CONFIRMED;
    }

    /**
     * Check if order is completed
     * @return boolean - true if delivered or refunded
     */
    public boolean isCompleted() {
        return status == OrderStatus.DELIVERED || status == OrderStatus.REFUNDED;
    }

    /**
     * Confirm the order
     */
    public void confirm() {
        validateStatusTransition(OrderStatus.CONFIRMED);
        this.status = OrderStatus.CONFIRMED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Start processing the order
     */
    public void startProcessing() {
        validateStatusTransition(OrderStatus.PROCESSING);
        this.status = OrderStatus.PROCESSING;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark order as shipped
     */
    public void ship() {
        validateStatusTransition(OrderStatus.SHIPPED);
        this.status = OrderStatus.SHIPPED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Mark order as delivered
     */
    public void deliver() {
        validateStatusTransition(OrderStatus.DELIVERED);
        this.status = OrderStatus.DELIVERED;
        this.completedAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Cancel the order
     */
    public void cancel() {
        if (!isCancellable()) {
            throw new IllegalStateException("Cannot cancel order in status: " + status);
        }
        this.status = OrderStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Process refund
     */
    public void refund() {
        if (status != OrderStatus.DELIVERED) {
            throw new IllegalStateException("Can only refund delivered orders");
        }
        this.status = OrderStatus.REFUNDED;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update notes
     * @param notes - new notes
     */
    public void updateNotes(String notes) {
        this.notes = notes;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Validate status transition
     * @param newStatus - target status
     */
    private void validateStatusTransition(OrderStatus newStatus) {
        boolean isValid = switch (newStatus) {
            case CONFIRMED -> status == OrderStatus.PENDING;
            case PROCESSING -> status == OrderStatus.CONFIRMED;
            case SHIPPED -> status == OrderStatus.PROCESSING;
            case DELIVERED -> status == OrderStatus.SHIPPED;
            default -> false;
        };

        if (!isValid) {
            throw new IllegalStateException(
                String.format("Cannot transition from %s to %s", status, newStatus)
            );
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Order order = (Order) o;
        return Objects.equals(id, order.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Order[id=%s, number=%s, status=%s, total=%s]",
            id, orderNumber, status, totalAmount);
    }

    /**
     * Builder class for constructing Order instances
     * Implements Builder Pattern for complex object creation
     */
    public static class Builder {

        private OrderId id;
        private String orderNumber;
        private UserId userId;
        private List<OrderItem> items = new ArrayList<>();
        private BigDecimal subtotal = BigDecimal.ZERO;
        private BigDecimal taxAmount = BigDecimal.ZERO;
        private BigDecimal shippingAmount = BigDecimal.ZERO;
        private BigDecimal discountAmount = BigDecimal.ZERO;
        private BigDecimal totalAmount = BigDecimal.ZERO;
        private OrderStatus status = OrderStatus.PENDING;
        private String shippingAddress;
        private String billingAddress;
        private String notes;

        /**
         * Create new builder with generated ID and order number
         */
        public Builder() {
            this.id = OrderId.generate();
            this.orderNumber = generateOrderNumber();
        }

        /**
         * Generate unique order number
         * @return String - order number like "ORD-20240114-XXXX"
         */
        private String generateOrderNumber() {
            String datePart = java.time.LocalDate.now()
                .format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
            String randomPart = String.format("%04d",
                (int) (Math.random() * 10000));
            return "ORD-" + datePart + "-" + randomPart;
        }

        /**
         * Set user ID
         * @param userId - user identifier
         * @return Builder - this builder
         */
        public Builder userId(UserId userId) {
            this.userId = userId;
            return this;
        }

        /**
         * Add item to order (temporary item without order reference)
         * @param product - product to add
         * @param quantity - quantity to add
         * @return Builder - this builder
         */
        public Builder addItem(Product product, int quantity) {
            // Create temporary item that will be linked when order is built
            // Use package-private constructor that allows null order
            OrderItem item = new OrderItem(null, product, quantity, true);
            this.items.add(item);
            recalculateTotals();
            return this;
        }

        /**
         * Set shipping address
         * @param address - shipping address
         * @return Builder - this builder
         */
        public Builder shippingAddress(String address) {
            this.shippingAddress = address;
            return this;
        }

        /**
         * Set billing address
         * @param address - billing address
         * @return Builder - this builder
         */
        public Builder billingAddress(String address) {
            this.billingAddress = address;
            return this;
        }

        /**
         * Set shipping amount
         * @param amount - shipping cost
         * @return Builder - this builder
         */
        public Builder shippingAmount(Money amount) {
            this.shippingAmount = amount.getAmount();
            recalculateTotals();
            return this;
        }

        /**
         * Apply discount
         * @param discount - discount amount
         * @return Builder - this builder
         */
        public Builder applyDiscount(Money discount) {
            this.discountAmount = discount.getAmount();
            recalculateTotals();
            return this;
        }

        /**
         * Set tax rate (Japanese consumption tax)
         * @param taxRate - tax rate (e.g., 0.10 for 10%)
         * @return Builder - this builder
         */
        public Builder withTaxRate(double taxRate) {
            this.taxAmount = subtotal.multiply(BigDecimal.valueOf(taxRate));
            recalculateTotals();
            return this;
        }

        /**
         * Add notes
         * @param notes - order notes
         * @return Builder - this builder
         */
        public Builder notes(String notes) {
            this.notes = notes;
            return this;
        }

        /**
         * Recalculate order totals
         */
        private void recalculateTotals() {
            this.subtotal = items.stream()
                .map(item -> item.getLineTotal().getAmount())
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            this.totalAmount = subtotal
                .add(taxAmount)
                .add(shippingAmount)
                .subtract(discountAmount);
        }

        /**
         * Build the Order instance
         * @return Order - constructed order
         */
        public Order build() {
            if (items.isEmpty()) {
                throw new IllegalStateException("Order must have at least one item");
            }

            recalculateTotals();
            return new Order(this);
        }
    }
}
