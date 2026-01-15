package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Email;
import com.portfolio.domain.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for Admin entity
 */
@DisplayName("Admin Entity Tests")
class AdminTest {

    @Test
    @DisplayName("Should create admin user")
    void shouldCreateAdminUser() {
        Admin admin = new Admin(
            UserId.generate(),
            Email.of("admin@example.com"),
            "admin_user",
            "hashed_password",
            "Engineering",
            3
        );

        assertThat(admin.getUserType()).isEqualTo("ADMIN");
        assertThat(admin.hasAdminPrivileges()).isTrue();
        assertThat(admin.getDepartment()).isEqualTo("Engineering");
        assertThat(admin.getAdminLevel()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should throw exception for invalid admin level")
    void shouldThrowExceptionForInvalidAdminLevel() {
        assertThatThrownBy(() -> new Admin(
            UserId.generate(),
            Email.of("admin@example.com"),
            "admin_user",
            "hashed_password",
            "Engineering",
            0
        )).isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Admin level must be between 1 and 5");
    }

    @Test
    @DisplayName("Should check if admin is super admin")
    void shouldCheckIfAdminIsSuperAdmin() {
        Admin regularAdmin = new Admin(
            UserId.generate(),
            Email.of("admin@example.com"),
            "admin_user",
            "hashed_password",
            "Engineering",
            3
        );

        Admin superAdmin = new Admin(
            UserId.generate(),
            Email.of("super@example.com"),
            "super_admin",
            "hashed_password",
            "IT",
            5
        );

        assertThat(regularAdmin.isSuperAdmin()).isFalse();
        assertThat(superAdmin.isSuperAdmin()).isTrue();
    }

    @Test
    @DisplayName("Should get display name")
    void shouldGetDisplayName() {
        Admin admin = new Admin(
            UserId.generate(),
            Email.of("admin@example.com"),
            "admin_user",
            "hashed_password",
            "Engineering",
            3
        );

        assertThat(admin.getDisplayName()).contains("[Admin]");
        assertThat(admin.getDisplayName()).contains("admin_user");
        assertThat(admin.getDisplayName()).contains("Engineering");
    }

    @Test
    @DisplayName("Should update admin department and level")
    void shouldUpdateAdminDepartmentAndLevel() {
        Admin admin = new Admin(
            UserId.generate(),
            Email.of("admin@example.com"),
            "admin_user",
            "hashed_password",
            "Engineering",
            3
        );

        admin.setDepartment("IT");
        admin.setAdminLevel(4);

        assertThat(admin.getDepartment()).isEqualTo("IT");
        assertThat(admin.getAdminLevel()).isEqualTo(4);
    }
}
