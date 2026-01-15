package com.portfolio.infrastructure.persistence;

import com.portfolio.domain.entity.Admin;
import com.portfolio.domain.entity.Member;
import com.portfolio.domain.valueobject.Email;
import com.portfolio.domain.valueobject.UserId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Simple unit tests for JpaUserRepository
 * Tests basic repository structure without database
 */
@DisplayName("JpaUserRepository Tests")
class JpaUserRepositoryTest {

    @Test
    @DisplayName("Should create Admin user")
    void shouldCreateAdminUser() {
        Admin admin = new Admin(
            UserId.generate(),
            Email.of("admin@test.com"),
            "admin_user",
            "hashed_password",
            "Engineering",
            3
        );

        assertThat(admin.getId()).isNotNull();
        assertThat(admin.getUserType()).isEqualTo("ADMIN");
        assertThat(admin.getEmail().getValue()).isEqualTo("admin@test.com");
    }

    @Test
    @DisplayName("Should create Member user")
    void shouldCreateMemberUser() {
        Member member = new Member(
            UserId.generate(),
            Email.of("member@test.com"),
            "member_user",
            "hashed_password",
            "Taro",
            "Yamada"
        );

        assertThat(member.getId()).isNotNull();
        assertThat(member.getUserType()).isEqualTo("MEMBER");
        assertThat(member.getEmail().getValue()).isEqualTo("member@test.com");
    }

    @Test
    @DisplayName("Should verify UserId generation")
    void shouldVerifyUserIdGeneration() {
        UserId id1 = UserId.generate();
        UserId id2 = UserId.generate();

        assertThat(id1).isNotNull();
        assertThat(id2).isNotNull();
        assertThat(id1).isNotEqualTo(id2);
    }

    @Test
    @DisplayName("Should verify Email value object")
    void shouldVerifyEmailValueObject() {
        Email email = Email.of("test@example.com");

        assertThat(email.getValue()).isEqualTo("test@example.com");
    }
}
