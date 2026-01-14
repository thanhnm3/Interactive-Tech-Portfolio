package com.portfolio.domain.repository;

import com.portfolio.domain.entity.Category;
import com.portfolio.domain.entity.Product;
import com.portfolio.domain.valueobject.Money;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Product aggregate
 * Demonstrates Repository Pattern with domain-specific queries
 */
public interface ProductRepository {

    /**
     * Find product by ID
     * @param id - product ID
     * @return Optional - product if found
     */
    Optional<Product> findById(String id);

    /**
     * Find product by SKU
     * @param sku - stock keeping unit
     * @return Optional - product if found
     */
    Optional<Product> findBySku(String sku);

    /**
     * Find products by category
     * @param category - category
     * @return List - products in category
     */
    List<Product> findByCategory(Category category);

    /**
     * Find products by category ID
     * @param categoryId - category ID
     * @return List - products in category
     */
    List<Product> findByCategoryId(String categoryId);

    /**
     * Find all active products
     * @return List - active products
     */
    List<Product> findAllActive();

    /**
     * Find featured products
     * @return List - featured products
     */
    List<Product> findFeatured();

    /**
     * Find products with low stock
     * @return List - products below minimum stock level
     */
    List<Product> findLowStock();

    /**
     * Find products in price range
     * @param minPrice - minimum price
     * @param maxPrice - maximum price
     * @return List - products in price range
     */
    List<Product> findByPriceRange(Money minPrice, Money maxPrice);

    /**
     * Search products by name
     * @param keyword - search keyword
     * @return List - matching products
     */
    List<Product> searchByName(String keyword);

    /**
     * Save product (create or update)
     * @param product - product to save
     * @return Product - saved product
     */
    Product save(Product product);

    /**
     * Delete product
     * @param product - product to delete
     */
    void delete(Product product);

    /**
     * Delete product by ID
     * @param id - product ID
     */
    void deleteById(String id);

    /**
     * Check if product exists by SKU
     * @param sku - SKU to check
     * @return boolean - true if exists
     */
    boolean existsBySku(String sku);

    /**
     * Count products by category
     * @param category - category
     * @return long - count
     */
    long countByCategory(Category category);

    /**
     * Count active products
     * @return long - count
     */
    long countActive();
}
