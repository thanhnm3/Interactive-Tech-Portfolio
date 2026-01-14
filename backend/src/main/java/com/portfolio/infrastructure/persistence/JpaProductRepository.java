package com.portfolio.infrastructure.persistence;

import com.portfolio.domain.entity.Category;
import com.portfolio.domain.entity.Product;
import com.portfolio.domain.repository.ProductRepository;
import com.portfolio.domain.valueobject.Money;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of ProductRepository
 * Demonstrates Repository Pattern adapter with search capabilities
 */
@Repository
@Transactional
public class JpaProductRepository implements ProductRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find product by ID
     * @param id - product ID
     * @return Optional - product if found
     */
    @Override
    public Optional<Product> findById(String id) {
        Product product = entityManager.find(Product.class, id);
        return Optional.ofNullable(product);
    }

    /**
     * Find product by SKU
     * @param sku - stock keeping unit
     * @return Optional - product if found
     */
    @Override
    public Optional<Product> findBySku(String sku) {
        TypedQuery<Product> query = entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.sku = :sku", Product.class);
        query.setParameter("sku", sku);

        List<Product> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find products by category
     * @param category - category
     * @return List - products in category
     */
    @Override
    public List<Product> findByCategory(Category category) {
        TypedQuery<Product> query = entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.category = :category", Product.class);
        query.setParameter("category", category);
        return query.getResultList();
    }

    /**
     * Find products by category ID
     * @param categoryId - category ID
     * @return List - products in category
     */
    @Override
    public List<Product> findByCategoryId(String categoryId) {
        TypedQuery<Product> query = entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.category.id = :categoryId", Product.class);
        query.setParameter("categoryId", categoryId);
        return query.getResultList();
    }

    /**
     * Find all active products
     * @return List - active products
     */
    @Override
    public List<Product> findAllActive() {
        TypedQuery<Product> query = entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.isActive = true ORDER BY p.createdAt DESC", Product.class);
        return query.getResultList();
    }

    /**
     * Find featured products
     * @return List - featured products
     */
    @Override
    public List<Product> findFeatured() {
        TypedQuery<Product> query = entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.isFeatured = true AND p.isActive = true", Product.class);
        return query.getResultList();
    }

    /**
     * Find products with low stock
     * @return List - products below minimum stock level
     */
    @Override
    public List<Product> findLowStock() {
        TypedQuery<Product> query = entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.stockQuantity <= p.minStockLevel AND p.isActive = true",
            Product.class);
        return query.getResultList();
    }

    /**
     * Find products in price range
     * @param minPrice - minimum price
     * @param maxPrice - maximum price
     * @return List - products in price range
     */
    @Override
    public List<Product> findByPriceRange(Money minPrice, Money maxPrice) {
        TypedQuery<Product> query = entityManager.createQuery(
            "SELECT p FROM Product p WHERE p.price >= :minPrice AND p.price <= :maxPrice AND p.isActive = true",
            Product.class);
        query.setParameter("minPrice", minPrice.getAmount());
        query.setParameter("maxPrice", maxPrice.getAmount());
        return query.getResultList();
    }

    /**
     * Search products by name
     * @param keyword - search keyword
     * @return List - matching products
     */
    @Override
    public List<Product> searchByName(String keyword) {
        TypedQuery<Product> query = entityManager.createQuery(
            "SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(:keyword) AND p.isActive = true",
            Product.class);
        query.setParameter("keyword", "%" + keyword + "%");
        return query.getResultList();
    }

    /**
     * Save product
     * @param product - product to save
     * @return Product - saved product
     */
    @Override
    public Product save(Product product) {
        if (findById(product.getId()).isEmpty()) {
            entityManager.persist(product);
            return product;
        } else {
            return entityManager.merge(product);
        }
    }

    /**
     * Delete product
     * @param product - product to delete
     */
    @Override
    public void delete(Product product) {
        entityManager.remove(entityManager.contains(product) ? product : entityManager.merge(product));
    }

    /**
     * Delete product by ID
     * @param id - product ID
     */
    @Override
    public void deleteById(String id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Check if product exists by SKU
     * @param sku - SKU to check
     * @return boolean - true if exists
     */
    @Override
    public boolean existsBySku(String sku) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.sku = :sku", Long.class);
        query.setParameter("sku", sku);
        return query.getSingleResult() > 0;
    }

    /**
     * Count products by category
     * @param category - category
     * @return long - count
     */
    @Override
    public long countByCategory(Category category) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.category = :category", Long.class);
        query.setParameter("category", category);
        return query.getSingleResult();
    }

    /**
     * Count active products
     * @return long - count
     */
    @Override
    public long countActive() {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(p) FROM Product p WHERE p.isActive = true", Long.class);
        return query.getSingleResult();
    }
}
