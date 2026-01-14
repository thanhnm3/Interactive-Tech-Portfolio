package com.portfolio.infrastructure.persistence;

import com.portfolio.domain.entity.Order;
import com.portfolio.domain.entity.Order.OrderStatus;
import com.portfolio.domain.repository.OrderRepository;
import com.portfolio.domain.valueobject.OrderId;
import com.portfolio.domain.valueobject.UserId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of OrderRepository
 * Demonstrates Repository Pattern adapter with complex queries
 */
@Repository
@Transactional
public class JpaOrderRepository implements OrderRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find order by ID
     * @param id - order ID
     * @return Optional - order if found
     */
    @Override
    public Optional<Order> findById(OrderId id) {
        Order order = entityManager.find(Order.class, id.getValue().toString());
        return Optional.ofNullable(order);
    }

    /**
     * Find order by order number
     * @param orderNumber - human-readable order number
     * @return Optional - order if found
     */
    @Override
    public Optional<Order> findByOrderNumber(String orderNumber) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.orderNumber = :orderNumber", Order.class);
        query.setParameter("orderNumber", orderNumber);

        List<Order> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find orders by user ID
     * @param userId - user ID
     * @return List - user's orders
     */
    @Override
    public List<Order> findByUserId(UserId userId) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.userId = :userId ORDER BY o.createdAt DESC", Order.class);
        query.setParameter("userId", userId.getValue().toString());
        return query.getResultList();
    }

    /**
     * Find orders by status
     * @param status - order status
     * @return List - orders with status
     */
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.status = :status ORDER BY o.createdAt DESC", Order.class);
        query.setParameter("status", status);
        return query.getResultList();
    }

    /**
     * Find orders by user and status
     * @param userId - user ID
     * @param status - order status
     * @return List - matching orders
     */
    @Override
    public List<Order> findByUserIdAndStatus(UserId userId, OrderStatus status) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.userId = :userId AND o.status = :status ORDER BY o.createdAt DESC",
            Order.class);
        query.setParameter("userId", userId.getValue().toString());
        query.setParameter("status", status);
        return query.getResultList();
    }

    /**
     * Find orders created in date range
     * @param startDate - start date
     * @param endDate - end date
     * @return List - orders in range
     */
    @Override
    public List<Order> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.createdAt >= :startDate AND o.createdAt < :endDate ORDER BY o.createdAt DESC",
            Order.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        return query.getResultList();
    }

    /**
     * Find recent orders
     * @param limit - maximum number of orders
     * @return List - recent orders
     */
    @Override
    public List<Order> findRecentOrders(int limit) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o ORDER BY o.createdAt DESC", Order.class);
        query.setMaxResults(limit);
        return query.getResultList();
    }

    /**
     * Find pending orders older than threshold
     * @param olderThan - threshold datetime
     * @return List - stale pending orders
     */
    @Override
    public List<Order> findStalePendingOrders(LocalDateTime olderThan) {
        TypedQuery<Order> query = entityManager.createQuery(
            "SELECT o FROM Order o WHERE o.status = :status AND o.createdAt < :olderThan",
            Order.class);
        query.setParameter("status", OrderStatus.PENDING);
        query.setParameter("olderThan", olderThan);
        return query.getResultList();
    }

    /**
     * Save order
     * @param order - order to save
     * @return Order - saved order
     */
    @Override
    public Order save(Order order) {
        if (findById(order.getId()).isEmpty()) {
            entityManager.persist(order);
            return order;
        } else {
            return entityManager.merge(order);
        }
    }

    /**
     * Delete order
     * @param order - order to delete
     */
    @Override
    public void delete(Order order) {
        entityManager.remove(entityManager.contains(order) ? order : entityManager.merge(order));
    }

    /**
     * Delete order by ID
     * @param id - order ID
     */
    @Override
    public void deleteById(OrderId id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Check if order number exists
     * @param orderNumber - order number to check
     * @return boolean - true if exists
     */
    @Override
    public boolean existsByOrderNumber(String orderNumber) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.orderNumber = :orderNumber", Long.class);
        query.setParameter("orderNumber", orderNumber);
        return query.getSingleResult() > 0;
    }

    /**
     * Count orders by status
     * @param status - order status
     * @return long - count
     */
    @Override
    public long countByStatus(OrderStatus status) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.status = :status", Long.class);
        query.setParameter("status", status);
        return query.getSingleResult();
    }

    /**
     * Count orders by user
     * @param userId - user ID
     * @return long - count
     */
    @Override
    public long countByUserId(UserId userId) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(o) FROM Order o WHERE o.userId = :userId", Long.class);
        query.setParameter("userId", userId.getValue().toString());
        return query.getSingleResult();
    }

    /**
     * Calculate total revenue in date range
     * @param startDate - start date
     * @param endDate - end date
     * @return double - total revenue
     */
    @Override
    public double calculateRevenue(LocalDateTime startDate, LocalDateTime endDate) {
        TypedQuery<BigDecimal> query = entityManager.createQuery(
            "SELECT COALESCE(SUM(o.totalAmount), 0) FROM Order o " +
            "WHERE o.createdAt >= :startDate AND o.createdAt < :endDate " +
            "AND o.status NOT IN (:cancelledStatus, :refundedStatus)",
            BigDecimal.class);
        query.setParameter("startDate", startDate);
        query.setParameter("endDate", endDate);
        query.setParameter("cancelledStatus", OrderStatus.CANCELLED);
        query.setParameter("refundedStatus", OrderStatus.REFUNDED);

        BigDecimal result = query.getSingleResult();
        return result != null ? result.doubleValue() : 0.0;
    }
}
