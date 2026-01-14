package com.portfolio.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Category entity for product classification
 * Supports hierarchical categories with parent-child relationships
 */
@Entity
@Table(name = "categories")
public class Category {

    @Id
    @Column(name = "id", columnDefinition = "uuid")
    private String id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Category> children = new ArrayList<>();

    @Column(name = "display_order")
    private Integer displayOrder;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Default constructor for JPA
     */
    protected Category() {
    }

    /**
     * Create category
     * @param name - category name
     * @param description - category description
     */
    public Category(String name, String description) {
        this.id = UUID.randomUUID().toString();
        this.name = Objects.requireNonNull(name, "Name cannot be null");
        this.description = description;
        this.slug = generateSlug(name);
        this.displayOrder = 0;
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Create subcategory with parent
     * @param name - category name
     * @param description - category description
     * @param parent - parent category
     */
    public Category(String name, String description, Category parent) {
        this(name, description);
        this.parent = parent;
        if (parent != null) {
            parent.getChildren().add(this);
        }
    }

    /**
     * Generate URL-friendly slug from name
     * @param name - category name
     * @return String - slug
     */
    private String generateSlug(String name) {
        return name.toLowerCase()
            .replaceAll("[^a-z0-9\\s-]", "")
            .replaceAll("\\s+", "-")
            .replaceAll("-+", "-")
            .trim();
    }

    /**
     * Get category ID
     * @return String - UUID string
     */
    public String getId() {
        return id;
    }

    /**
     * Get category name
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
     * Get URL slug
     * @return String - slug
     */
    public String getSlug() {
        return slug;
    }

    /**
     * Get parent category
     * @return Category - parent or null
     */
    public Category getParent() {
        return parent;
    }

    /**
     * Get child categories
     * @return List of child categories
     */
    public List<Category> getChildren() {
        return children;
    }

    /**
     * Get display order
     * @return Integer - order for sorting
     */
    public Integer getDisplayOrder() {
        return displayOrder;
    }

    /**
     * Check if category is active
     * @return boolean - active status
     */
    public boolean isActive() {
        return isActive;
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
     * Check if this is a root category
     * @return boolean - true if no parent
     */
    public boolean isRootCategory() {
        return parent == null;
    }

    /**
     * Get the depth level in hierarchy
     * @return int - depth level (0 for root)
     */
    public int getDepthLevel() {
        int depth = 0;
        Category current = this.parent;

        while (current != null) {
            depth++;
            current = current.getParent();
        }

        return depth;
    }

    /**
     * Get full path from root to this category
     * @return String - path like "Electronics > Computers > Laptops"
     */
    public String getFullPath() {
        List<String> pathList = new ArrayList<>();
        Category current = this;

        while (current != null) {
            pathList.add(0, current.getName());
            current = current.getParent();
        }

        return String.join(" > ", pathList);
    }

    /**
     * Update category details
     * @param name - new name
     * @param description - new description
     */
    public void updateDetails(String name, String description) {
        this.name = name;
        this.description = description;
        this.slug = generateSlug(name);
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Set display order
     * @param displayOrder - new display order
     */
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Activate category
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deactivate category
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Move to different parent
     * @param newParent - new parent category
     */
    public void moveTo(Category newParent) {
        if (this.parent != null) {
            this.parent.getChildren().remove(this);
        }
        this.parent = newParent;
        if (newParent != null) {
            newParent.getChildren().add(this);
        }
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return Objects.equals(id, category.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Category[id=%s, name=%s, slug=%s]", id, name, slug);
    }
}
