package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Email;
import com.portfolio.domain.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Unit tests for User abstract class
 * Tests common functionality across user types
 */
@DisplayName("User Entity Tests")
class UserTest {

    @Test
    @DisplayName("Should test user polymorphism through Admin")
    void shouldTestUserPolymorphismThroughAdmin() {
        Admin admin = new Admin(
            UserId.generate(),
            Email.of("admin@test.com"),
            "admin_user",
            "hashed_password",
            "Engineering",
            3
        );

        assertThat(admin).isInstanceOf(User.class);
        assertThat(admin.getUserType()).isEqualTo("ADMIN");
        assertThat(admin.hasAdminPrivileges()).isTrue();
    }

    @Test
    @DisplayName("Should test user polymorphism through Member")
    void shouldTestUserPolymorphismThroughMember() {
        Member member = new Member(
            UserId.generate(),
            Email.of("member@test.com"),
            "member_user",
            "hashed_password",
            "Taro",
            "Yamada"
        );

        assertThat(member).isInstanceOf(User.class);
        assertThat(member.getUserType()).isEqualTo("MEMBER");
        assertThat(member.hasAdminPrivileges()).isFalse();
    }

    @Test
    @DisplayName("Should test user polymorphism through Guest")
    void shouldTestUserPolymorphismThroughGuest() {
        Guest guest = new Guest(
            UserId.generate(),
            "session-123",
            "192.168.1.1",
            "Mozilla/5.0"
        );

        assertThat(guest).isInstanceOf(User.class);
        assertThat(guest.getUserType()).isEqualTo("GUEST");
        assertThat(guest.hasAdminPrivileges()).isFalse();
    }

    @Test
    @DisplayName("Should activate and deactivate user")
    void shouldActivateAndDeactivateUser() {
        Admin admin = new Admin(
            UserId.generate(),
            Email.of("admin@test.com"),
            "admin_user",
            "hashed_password",
            "Engineering",
            3
        );

        admin.deactivate();
        assertThat(admin.isActive()).isFalse();

        admin.activate();
        assertThat(admin.isActive()).isTrue();
    }

    @Test
    @DisplayName("Should update email")
    void shouldUpdateEmail() {
        Admin admin = new Admin(
            UserId.generate(),
            Email.of("old@test.com"),
            "admin_user",
            "hashed_password",
            "Engineering",
            3
        );

        admin.updateEmail(Email.of("new@test.com"));

        assertThat(admin.getEmail().getValue()).isEqualTo("new@test.com");
    }

    @Test
    @DisplayName("Should update password")
    void shouldUpdatePassword() {
        Admin admin = new Admin(
            UserId.generate(),
            Email.of("admin@test.com"),
            "admin_user",
            "old_hash",
            "Engineering",
            3
        );

        admin.updatePassword("new_hash");

        assertThat(admin.getPasswordHash()).isEqualTo("new_hash");
    }
}
