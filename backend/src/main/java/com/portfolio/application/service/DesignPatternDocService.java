package com.portfolio.application.service;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Service for design pattern documentation and explanations
 * Provides educational content about implemented patterns
 */
@Service
public class DesignPatternDocService {

    private final Map<String, PatternDoc> patternDocs;

    /**
     * Pattern documentation record
     */
    public record PatternDoc(
        String name,
        String type,
        String intent,
        String problem,
        String solution,
        List<String> participants,
        String realWorldExample,
        String codeExample,
        List<String> relatedPatterns
    ) {}

    /**
     * Constructor initializes pattern documentation
     */
    public DesignPatternDocService() {
        this.patternDocs = initializePatternDocs();
    }

    /**
     * Initialize design pattern documentation
     * @return Map - pattern documentation by key
     */
    private Map<String, PatternDoc> initializePatternDocs() {
        Map<String, PatternDoc> docs = new HashMap<>();

        // Builder Pattern
        docs.put("builder", new PatternDoc(
            "Builder Pattern",
            "Creational",
            "Separate the construction of a complex object from its representation so that the same construction process can create different representations.",
            "Creating complex objects with many optional parameters leads to constructor explosion (telescoping constructor anti-pattern). Direct construction makes code hard to read and maintain.",
            "Extract the object construction code into a separate Builder class with methods for each construction step. The build() method returns the final product.",
            List.of("Builder", "ConcreteBuilder", "Director", "Product"),
            "Order creation in e-commerce: An order has required fields (items) and optional fields (shipping, discount, notes). Builder allows step-by-step construction.",
            """
                Order order = new Order.Builder()
                    .userId(currentUser.getId())
                    .addItem(product, quantity)
                    .shippingAddress("Tokyo, Japan")
                    .applyDiscount(discount)
                    .notes("Gift wrap please")
                    .build();
                """,
            List.of("Abstract Factory", "Prototype", "Fluent Interface")
        ));

        // Factory Pattern
        docs.put("factory", new PatternDoc(
            "Factory Method Pattern",
            "Creational",
            "Define an interface for creating an object, but let subclasses decide which class to instantiate.",
            "Code becomes coupled to specific classes, making it difficult to extend or modify object creation logic.",
            "Replace direct object construction with a factory method that subclasses can override to change the type of objects created.",
            List.of("Product", "ConcreteProduct", "Creator", "ConcreteCreator"),
            "User creation: Different user types (Admin, Member, Guest) require different initialization. Factory encapsulates this logic.",
            """
                public class UserFactory {
                    public static User createUser(String type, UserDto dto) {
                        return switch (type.toUpperCase()) {
                            case "ADMIN" -> new Admin(dto.email(), dto.department());
                            case "MEMBER" -> new Member(dto.email(), dto.name());
                            default -> new Guest(dto.sessionId());
                        };
                    }
                }
                """,
            List.of("Abstract Factory", "Builder", "Prototype")
        ));

        // Strategy Pattern
        docs.put("strategy", new PatternDoc(
            "Strategy Pattern",
            "Behavioral",
            "Define a family of algorithms, encapsulate each one, and make them interchangeable. Strategy lets the algorithm vary independently from clients that use it.",
            "Multiple conditional statements (if/switch) for selecting algorithms make code hard to maintain and violate Open/Closed Principle.",
            "Extract each algorithm into separate classes with a common interface. Context holds reference to strategy and delegates algorithm execution.",
            List.of("Strategy", "ConcreteStrategy", "Context"),
            "Payment processing: Different payment methods (Credit Card, PayPay, Bank Transfer) have different processing logic. Strategy allows runtime selection.",
            """
                public interface PaymentStrategy {
                    PaymentResult process(Order order);
                }
                
                public class CreditCardPayment implements PaymentStrategy {
                    @Override
                    public PaymentResult process(Order order) {
                        // Credit card specific logic
                        return PaymentResult.success(transactionId);
                    }
                }
                
                // Usage
                PaymentContext context = new PaymentContext();
                context.setStrategy(new CreditCardPayment());
                PaymentResult result = context.executePayment(order);
                """,
            List.of("State", "Command", "Template Method")
        ));

        // Repository Pattern
        docs.put("repository", new PatternDoc(
            "Repository Pattern",
            "Architectural",
            "Mediate between the domain and data mapping layers using a collection-like interface for accessing domain objects.",
            "Direct database access from business logic creates tight coupling, makes testing difficult, and spreads SQL throughout the codebase.",
            "Create repository interfaces that act like in-memory collections. Implementations handle actual data access, hiding persistence details from domain.",
            List.of("Repository Interface", "Repository Implementation", "Entity", "Data Mapper"),
            "Product repository: Business logic works with Product domain objects. Repository handles JPA/SQL details, enabling easy testing with mock repositories.",
            """
                public interface ProductRepository {
                    Product findById(ProductId id);
                    List<Product> findByCategory(Category category);
                    void save(Product product);
                    void delete(Product product);
                }
                
                @Repository
                public class JpaProductRepository implements ProductRepository {
                    private final JpaProductDataRepository jpaRepo;
                    
                    @Override
                    public Product findById(ProductId id) {
                        return jpaRepo.findById(id.getValue())
                            .map(this::toDomain)
                            .orElse(null);
                    }
                }
                """,
            List.of("Unit of Work", "Data Mapper", "DAO")
        ));

        // Value Object Pattern
        docs.put("value-object", new PatternDoc(
            "Value Object Pattern",
            "Domain-Driven Design",
            "A small object that represents a descriptive aspect of the domain with no conceptual identity.",
            "Primitive obsession leads to scattered validation logic, unclear code intent, and type-unsafe operations.",
            "Create immutable objects that encapsulate related values with their validation and business logic. Equality is based on values, not identity.",
            List.of("Value Object"),
            "Money in e-commerce: Instead of BigDecimal amount and String currency, Money value object encapsulates both with currency validation and arithmetic operations.",
            """
                public final class Money {
                    private final BigDecimal amount;
                    private final Currency currency;
                    
                    public Money add(Money other) {
                        validateSameCurrency(other);
                        return new Money(
                            this.amount.add(other.amount),
                            this.currency
                        );
                    }
                    
                    @Override
                    public boolean equals(Object o) {
                        // Value-based equality
                        return amount.equals(other.amount) 
                            && currency.equals(other.currency);
                    }
                }
                """,
            List.of("Entity", "Aggregate", "Domain Event")
        ));

        // Singleton Pattern
        docs.put("singleton", new PatternDoc(
            "Singleton Pattern",
            "Creational",
            "Ensure a class has only one instance and provide a global point of access to it.",
            "Some resources (database connections, configuration) should have only one instance. Multiple instances would cause inconsistency or waste resources.",
            "Make constructor private, store instance in static field, provide static method to get instance. In Spring, beans are singleton by default.",
            List.of("Singleton"),
            "Application configuration: Config should be loaded once and shared. Spring @Service and @Component are singletons by default.",
            """
                // Traditional Java Singleton
                public class AppConfig {
                    private static volatile AppConfig instance;
                    
                    private AppConfig() {}
                    
                    public static AppConfig getInstance() {
                        if (instance == null) {
                            synchronized (AppConfig.class) {
                                if (instance == null) {
                                    instance = new AppConfig();
                                }
                            }
                        }
                        return instance;
                    }
                }
                
                // Spring way (preferred)
                @Component
                public class AppConfig {
                    // Spring manages singleton lifecycle
                }
                """,
            List.of("Factory", "Prototype", "Dependency Injection")
        ));

        return docs;
    }

    /**
     * Get pattern documentation by key
     * @param patternKey - pattern key
     * @return PatternDoc - pattern documentation or null
     */
    public PatternDoc getPatternDoc(String patternKey) {
        return patternDocs.get(patternKey.toLowerCase());
    }

    /**
     * Get all pattern documentation
     * @return Map - all pattern docs
     */
    public Map<String, PatternDoc> getAllPatternDocs() {
        return new HashMap<>(patternDocs);
    }

    /**
     * Get patterns by type
     * @param type - pattern type (Creational, Structural, Behavioral)
     * @return List - matching patterns
     */
    public List<PatternDoc> getPatternsByType(String type) {
        return patternDocs.values().stream()
            .filter(p -> type.equalsIgnoreCase(p.type()))
            .toList();
    }

    /**
     * Get available pattern keys
     * @return List - pattern keys
     */
    public List<String> getAvailablePatternKeys() {
        return List.copyOf(patternDocs.keySet());
    }

    /**
     * Get available pattern types
     * @return List - pattern types
     */
    public List<String> getAvailablePatternTypes() {
        return patternDocs.values().stream()
            .map(PatternDoc::type)
            .distinct()
            .toList();
    }

    /**
     * Search patterns by keyword
     * @param keyword - search keyword
     * @return List - matching patterns
     */
    public List<PatternDoc> searchPatterns(String keyword) {
        String lowerKeyword = keyword.toLowerCase();
        return patternDocs.values().stream()
            .filter(p -> p.name().toLowerCase().contains(lowerKeyword) ||
                        p.intent().toLowerCase().contains(lowerKeyword) ||
                        p.problem().toLowerCase().contains(lowerKeyword))
            .toList();
    }
}
