package com.portfolio.application.service;

import com.portfolio.application.dto.CodeSnippetDto;
import com.portfolio.application.dto.SqlCaptureDto;
import com.portfolio.infrastructure.persistence.HibernateQueryInterceptor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for displaying Java code snippets and captured SQL
 * Provides code examples for educational purposes
 */
@Service
public class CodeDisplayService {

    private final HibernateQueryInterceptor queryInterceptor;
    private final Map<String, CodeSnippetDto> codeSnippets;

    /**
     * Constructor with dependency injection
     * @param queryInterceptor - Hibernate query interceptor
     */
    public CodeDisplayService(HibernateQueryInterceptor queryInterceptor) {
        this.queryInterceptor = queryInterceptor;
        this.codeSnippets = initializeCodeSnippets();
    }

    /**
     * Initialize code snippets for display
     * @return Map - code snippets by category
     */
    private Map<String, CodeSnippetDto> initializeCodeSnippets() {
        Map<String, CodeSnippetDto> snippets = new HashMap<>();

        // JPA Repository example
        snippets.put("jpa-repository", new CodeSnippetDto.Builder()
            .title("JPA Repository Pattern")
            .language("java")
            .sourceCode("""
                @Repository
                public interface ProductRepository extends JpaRepository<Product, UUID> {
                    
                    /**
                     * Find products by category with pagination
                     * @param categoryId - category identifier
                     * @param pageable - pagination info
                     * @return Page - paginated products
                     */
                    Page<Product> findByCategoryId(UUID categoryId, Pageable pageable);
                    
                    /**
                     * Find products by name containing keyword
                     * @param keyword - search keyword
                     * @return List - matching products
                     */
                    @Query("SELECT p FROM Product p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%'))")
                    List<Product> searchByName(@Param("keyword") String keyword);
                    
                    /**
                     * Find featured products with stock
                     * @return List - featured products in stock
                     */
                    @Query("SELECT p FROM Product p WHERE p.isFeatured = true AND p.stockQuantity > 0")
                    List<Product> findFeaturedInStock();
                    
                    /**
                     * Update stock quantity
                     * @param productId - product ID
                     * @param quantity - new quantity
                     */
                    @Modifying
                    @Query("UPDATE Product p SET p.stockQuantity = :quantity WHERE p.id = :productId")
                    void updateStock(@Param("productId") UUID productId, @Param("quantity") Integer quantity);
                }
                """)
            .description("Spring Data JPA repository with custom queries demonstrating JPQL, pagination, and modifying queries")
            .highlightedLines(List.of("10", "17", "24", "32"))
            .category("persistence")
            .build());

        // Entity with inheritance
        snippets.put("entity-inheritance", new CodeSnippetDto.Builder()
            .title("JPA Entity Inheritance (Single Table)")
            .language("java")
            .sourceCode("""
                @Entity
                @Table(name = "users")
                @Inheritance(strategy = InheritanceType.SINGLE_TABLE)
                @DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
                public abstract class User {
                    
                    @Id
                    @Column(name = "id", columnDefinition = "uuid")
                    private String id;
                    
                    @Column(name = "email", nullable = false, unique = true)
                    private String email;
                    
                    @Column(name = "username", nullable = false)
                    private String username;
                    
                    // Abstract methods for polymorphism
                    public abstract String getUserType();
                    public abstract boolean hasAdminPrivileges();
                }
                
                @Entity
                @DiscriminatorValue("ADMIN")
                public class Admin extends User {
                    
                    @Column(name = "admin_level")
                    private Integer adminLevel;
                    
                    @Override
                    public String getUserType() {
                        return "ADMIN";
                    }
                    
                    @Override
                    public boolean hasAdminPrivileges() {
                        return true;
                    }
                }
                
                @Entity
                @DiscriminatorValue("MEMBER")
                public class Member extends User {
                    
                    @Column(name = "membership_tier")
                    private String membershipTier;
                    
                    @Override
                    public String getUserType() {
                        return "MEMBER";
                    }
                    
                    @Override
                    public boolean hasAdminPrivileges() {
                        return false;
                    }
                }
                """)
            .description("JPA Single Table Inheritance pattern with discriminator column for User hierarchy")
            .highlightedLines(List.of("3", "4", "23", "40"))
            .category("entity")
            .build());

        // Value Object
        snippets.put("value-object", new CodeSnippetDto.Builder()
            .title("Value Object Pattern")
            .language("java")
            .sourceCode("""
                /**
                 * Value Object representing monetary amount
                 * Immutable and supports arithmetic operations
                 */
                public class Money {
                    
                    private final BigDecimal amount;
                    private final Currency currency;
                    
                    private Money(BigDecimal amount, Currency currency) {
                        this.amount = amount.setScale(2, RoundingMode.HALF_UP);
                        this.currency = Objects.requireNonNull(currency);
                    }
                    
                    public static Money of(BigDecimal amount) {
                        return new Money(amount, Currency.getInstance("JPY"));
                    }
                    
                    public static Money zero() {
                        return new Money(BigDecimal.ZERO, Currency.getInstance("JPY"));
                    }
                    
                    public Money add(Money other) {
                        validateSameCurrency(other);
                        return new Money(this.amount.add(other.amount), this.currency);
                    }
                    
                    public Money multiply(int multiplier) {
                        return new Money(this.amount.multiply(BigDecimal.valueOf(multiplier)), this.currency);
                    }
                    
                    private void validateSameCurrency(Money other) {
                        if (!this.currency.equals(other.currency)) {
                            throw new IllegalArgumentException("Currency mismatch");
                        }
                    }
                    
                    @Override
                    public boolean equals(Object o) {
                        if (this == o) return true;
                        if (o == null || getClass() != o.getClass()) return false;
                        Money money = (Money) o;
                        return amount.compareTo(money.amount) == 0 &&
                               Objects.equals(currency, money.currency);
                    }
                    
                    @Override
                    public int hashCode() {
                        return Objects.hash(amount, currency);
                    }
                }
                """)
            .description("Immutable Value Object with factory methods and business logic encapsulation")
            .highlightedLines(List.of("7", "8", "10", "15", "23"))
            .category("ddd")
            .build());

        // Builder Pattern
        snippets.put("builder-pattern", new CodeSnippetDto.Builder()
            .title("Builder Pattern for Complex Objects")
            .language("java")
            .sourceCode("""
                public class Order {
                    private final OrderId id;
                    private final List<OrderItem> items;
                    private final Money totalAmount;
                    private final OrderStatus status;
                    
                    private Order(Builder builder) {
                        this.id = builder.id;
                        this.items = List.copyOf(builder.items);
                        this.totalAmount = builder.totalAmount;
                        this.status = builder.status;
                    }
                    
                    public static class Builder {
                        private OrderId id;
                        private List<OrderItem> items = new ArrayList<>();
                        private Money totalAmount = Money.zero();
                        private OrderStatus status = OrderStatus.PENDING;
                        
                        public Builder() {
                            this.id = OrderId.generate();
                        }
                        
                        public Builder addItem(Product product, int quantity) {
                            items.add(new OrderItem(product, quantity));
                            recalculateTotal();
                            return this;
                        }
                        
                        public Builder withShipping(Money shippingCost) {
                            this.totalAmount = totalAmount.add(shippingCost);
                            return this;
                        }
                        
                        private void recalculateTotal() {
                            this.totalAmount = items.stream()
                                .map(OrderItem::getLineTotal)
                                .reduce(Money.zero(), Money::add);
                        }
                        
                        public Order build() {
                            if (items.isEmpty()) {
                                throw new IllegalStateException("Order must have items");
                            }
                            return new Order(this);
                        }
                    }
                }
                
                // Usage
                Order order = new Order.Builder()
                    .addItem(laptop, 1)
                    .addItem(mouse, 2)
                    .withShipping(Money.of(500))
                    .build();
                """)
            .description("Builder pattern for constructing complex Order objects with validation")
            .highlightedLines(List.of("7", "14", "24", "41"))
            .category("pattern")
            .build());

        return snippets;
    }

    /**
     * Get code snippet by key
     * @param key - snippet key
     * @return CodeSnippetDto - code snippet or null
     */
    public CodeSnippetDto getCodeSnippet(String key) {
        return codeSnippets.get(key);
    }

    /**
     * Get all code snippets
     * @return Map - all snippets
     */
    public Map<String, CodeSnippetDto> getAllCodeSnippets() {
        return new HashMap<>(codeSnippets);
    }

    /**
     * Get snippets by category
     * @param category - category to filter
     * @return List - matching snippets
     */
    public List<CodeSnippetDto> getSnippetsByCategory(String category) {
        return codeSnippets.values().stream()
            .filter(s -> category.equalsIgnoreCase(s.getCategory()))
            .toList();
    }

    /**
     * Start SQL capture session
     */
    public void startSqlCapture() {
        queryInterceptor.startCapturing();
    }

    /**
     * Stop SQL capture session
     */
    public void stopSqlCapture() {
        queryInterceptor.stopCapturing();
    }

    /**
     * Get captured SQL queries
     * @return List - captured queries
     */
    public List<SqlCaptureDto> getCapturedSql() {
        return queryInterceptor.getCapturedQueries();
    }

    /**
     * Clear captured SQL
     */
    public void clearCapturedSql() {
        queryInterceptor.clearCapturedQueries();
    }

    /**
     * Get available snippet keys
     * @return List - snippet keys
     */
    public List<String> getAvailableSnippetKeys() {
        return List.copyOf(codeSnippets.keySet());
    }

    /**
     * Get available categories
     * @return List - categories
     */
    public List<String> getAvailableCategories() {
        return codeSnippets.values().stream()
            .map(CodeSnippetDto::getCategory)
            .distinct()
            .toList();
    }
}
