package com.portfolio.domain.repository;

import com.portfolio.domain.entity.Order;
import com.portfolio.domain.entity.Order.OrderStatus;
import com.portfolio.domain.valueobject.OrderId;
import com.portfolio.domain.valueobject.UserId;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order aggregate
 * Demonstrates Repository Pattern with complex query methods
 */
public interface OrderRepository {

    /**
     * Find order by ID
     * @param id - order ID
     * @return Optional - order if found
     */
    Optional<Order> findById(OrderId id);

    /**
     * Find order by order number
     * @param orderNumber - human-readable order number
     * @return Optional - order if found
     */
    Optional<Order> findByOrderNumber(String orderNumber);

    /**
     * Find orders by user ID
     * @param userId - user ID
     * @return List - user's orders
     */
    List<Order> findByUserId(UserId userId);

    /**
     * Find orders by status
     * @param status - order status
     * @return List - orders with status
     */
    List<Order> findByStatus(OrderStatus status);

    /**
     * Find orders by user and status
     * @param userId - user ID
     * @param status - order status
     * @return List - matching orders
     */
    List<Order> findByUserIdAndStatus(UserId userId, OrderStatus status);

    /**
     * Find orders created in date range
     * @param startDate - start date
     * @param endDate - end date
     * @return List - orders in range
     */
    List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Find recent orders
     * @param limit - maximum number of orders
     * @return List - recent orders
     */
    List<Order> findRecentOrders(int limit);

    /**
     * Find pending orders older than threshold
     * @param olderThan - threshold datetime
     * @return List - stale pending orders
     */
    List<Order> findStalePendingOrders(LocalDateTime olderThan);

    /**
     * Save order (create or update)
     * @param order - order to save
     * @return Order - saved order
     */
    Order save(Order order);

    /**
     * Delete order
     * @param order - order to delete
     */
    void delete(Order order);

    /**
     * Delete order by ID
     * @param id - order ID
     */
    void deleteById(OrderId id);

    /**
     * Check if order number exists
     * @param orderNumber - order number to check
     * @return boolean - true if exists
     */
    boolean existsByOrderNumber(String orderNumber);

    /**
     * Count orders by status
     * @param status - order status
     * @return long - count
     */
    long countByStatus(OrderStatus status);

    /**
     * Count orders by user
     * @param userId - user ID
     * @return long - count
     */
    long countByUserId(UserId userId);

    /**
     * Calculate total revenue in date range
     * @param startDate - start date
     * @param endDate - end date
     * @return double - total revenue
     */
    double calculateRevenue(LocalDateTime startDate, LocalDateTime endDate);
}
