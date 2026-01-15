package com.portfolio.domain.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for Category entity
 */
@DisplayName("Category Entity Tests")
class CategoryTest {

    @Test
    @DisplayName("Should create category with name and description")
    void shouldCreateCategoryWithNameAndDescription() {
        Category category = new Category("Electronics", "Electronic products");

        assertThat(category.getName()).isEqualTo("Electronics");
        assertThat(category.getDescription()).isEqualTo("Electronic products");
        assertThat(category.getSlug()).isEqualTo("electronics");
        assertThat(category.isActive()).isTrue();
        assertThat(category.getId()).isNotNull();
    }

    @Test
    @DisplayName("Should generate slug from name")
    void shouldGenerateSlugFromName() {
        Category category1 = new Category("Computer Accessories", "Accessories");
        Category category2 = new Category("Home & Garden", "Home items");

        assertThat(category1.getSlug()).isEqualTo("computer-accessories");
        assertThat(category2.getSlug()).isEqualTo("home-garden");
    }

    @Test
    @DisplayName("Should create subcategory with parent")
    void shouldCreateSubcategoryWithParent() {
        Category parent = new Category("Electronics", "Electronic products");
        Category child = new Category("Laptops", "Laptop computers", parent);

        assertThat(child.getParent()).isEqualTo(parent);
        assertThat(parent.getChildren()).contains(child);
    }

    @Test
    @DisplayName("Should check if category is root")
    void shouldCheckIfCategoryIsRoot() {
        Category root = new Category("Electronics", "Electronic products");
        Category child = new Category("Laptops", "Laptops", root);

        assertThat(root.isRootCategory()).isTrue();
        assertThat(child.isRootCategory()).isFalse();
    }

    @Test
    @DisplayName("Should calculate depth level")
    void shouldCalculateDepthLevel() {
        Category root = new Category("Electronics", "Electronic products");
        Category level1 = new Category("Computers", "Computers", root);
        Category level2 = new Category("Laptops", "Laptops", level1);

        assertThat(root.getDepthLevel()).isEqualTo(0);
        assertThat(level1.getDepthLevel()).isEqualTo(1);
        assertThat(level2.getDepthLevel()).isEqualTo(2);
    }

    @Test
    @DisplayName("Should get full path from root")
    void shouldGetFullPathFromRoot() {
        Category root = new Category("Electronics", "Electronic products");
        Category level1 = new Category("Computers", "Computers", root);
        Category level2 = new Category("Laptops", "Laptops", level1);

        assertThat(level2.getFullPath()).isEqualTo("Electronics > Computers > Laptops");
    }

    @Test
    @DisplayName("Should update category details")
    void shouldUpdateCategoryDetails() {
        Category category = new Category("Electronics", "Electronic products");
        category.updateDetails("New Name", "New Description");

        assertThat(category.getName()).isEqualTo("New Name");
        assertThat(category.getDescription()).isEqualTo("New Description");
        assertThat(category.getSlug()).isEqualTo("new-name");
    }

    @Test
    @DisplayName("Should activate and deactivate category")
    void shouldActivateAndDeactivateCategory() {
        Category category = new Category("Electronics", "Electronic products");
        category.deactivate();

        assertThat(category.isActive()).isFalse();

        category.activate();
        assertThat(category.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should move category to different parent")
    void shouldMoveCategoryToDifferentParent() {
        Category parent1 = new Category("Electronics", "Electronic products");
        Category parent2 = new Category("Computers", "Computer products");
        Category child = new Category("Laptops", "Laptops", parent1);

        child.moveTo(parent2);

        assertThat(child.getParent()).isEqualTo(parent2);
        assertThat(parent1.getChildren()).doesNotContain(child);
        assertThat(parent2.getChildren()).contains(child);
    }
}
