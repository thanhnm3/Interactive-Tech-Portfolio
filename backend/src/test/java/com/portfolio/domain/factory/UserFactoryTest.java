package com.portfolio.domain.factory;

import com.portfolio.domain.entity.Admin;
import com.portfolio.domain.entity.Guest;
import com.portfolio.domain.entity.Member;
import com.portfolio.domain.entity.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Unit tests for UserFactory
 */
@DisplayName("UserFactory Tests")
class UserFactoryTest {

    private final UserFactory factory = new UserFactory();

    @Test
    @DisplayName("Should create admin user")
    void shouldCreateAdminUser() {
        UserFactory.UserCreateDto dto = new UserFactory.UserCreateDto(
            "admin@example.com",
            "admin_user",
            "hashed_password",
            null,
            null,
            "Engineering",
            3,
            null,
            null,
            null
        );

        User user = factory.createUser(UserFactory.UserType.ADMIN, dto);

        assertThat(user).isInstanceOf(Admin.class);
        assertThat(user.getUserType()).isEqualTo("ADMIN");
        assertThat(user.hasAdminPrivileges()).isTrue();
    }

    @Test
    @DisplayName("Should create member user")
    void shouldCreateMemberUser() {
        UserFactory.UserCreateDto dto = new UserFactory.UserCreateDto(
            "member@example.com",
            "member_user",
            "hashed_password",
            "Taro",
            "Yamada",
            null,
            null,
            null,
            null,
            null
        );

        User user = factory.createUser(UserFactory.UserType.MEMBER, dto);

        assertThat(user).isInstanceOf(Member.class);
        assertThat(user.getUserType()).isEqualTo("MEMBER");
        assertThat(user.hasAdminPrivileges()).isFalse();
    }

    @Test
    @DisplayName("Should create guest user")
    void shouldCreateGuestUser() {
        UserFactory.UserCreateDto dto = new UserFactory.UserCreateDto(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            "session-123",
            "192.168.1.1",
            "Mozilla/5.0"
        );

        User user = factory.createUser(UserFactory.UserType.GUEST, dto);

        assertThat(user).isInstanceOf(Guest.class);
        assertThat(user.getUserType()).isEqualTo("GUEST");
    }

    @Test
    @DisplayName("Should create user from string type")
    void shouldCreateUserFromStringType() {
        UserFactory.UserCreateDto dto = UserFactory.createDemoAdminDto();
        User user = factory.createUser("ADMIN", dto);

        assertThat(user).isInstanceOf(Admin.class);
    }

    @Test
    @DisplayName("Should throw exception for invalid admin data")
    void shouldThrowExceptionForInvalidAdminData() {
        UserFactory.UserCreateDto dto = new UserFactory.UserCreateDto(
            null,
            "admin_user",
            "hashed_password",
            null,
            null,
            "Engineering",
            3,
            null,
            null,
            null
        );

        assertThatThrownBy(() -> factory.createUser(UserFactory.UserType.ADMIN, dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Email is required");
    }

    @Test
    @DisplayName("Should throw exception for invalid admin level")
    void shouldThrowExceptionForInvalidAdminLevel() {
        UserFactory.UserCreateDto dto = new UserFactory.UserCreateDto(
            "admin@example.com",
            "admin_user",
            "hashed_password",
            null,
            null,
            "Engineering",
            10,
            null,
            null,
            null
        );

        assertThatThrownBy(() -> factory.createUser(UserFactory.UserType.ADMIN, dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Admin level must be between 1 and 5");
    }

    @Test
    @DisplayName("Should throw exception for invalid guest data")
    void shouldThrowExceptionForInvalidGuestData() {
        UserFactory.UserCreateDto dto = new UserFactory.UserCreateDto(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null
        );

        assertThatThrownBy(() -> factory.createUser(UserFactory.UserType.GUEST, dto))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Session ID is required");
    }

    @Test
    @DisplayName("Should create demo admin DTO")
    void shouldCreateDemoAdminDto() {
        UserFactory.UserCreateDto dto = UserFactory.createDemoAdminDto();

        assertThat(dto.email()).isEqualTo("admin@portfolio.example.com");
        assertThat(dto.username()).isEqualTo("admin_user");
        assertThat(dto.department()).isEqualTo("Engineering");
        assertThat(dto.adminLevel()).isEqualTo(3);
    }

    @Test
    @DisplayName("Should create demo member DTO")
    void shouldCreateDemoMemberDto() {
        UserFactory.UserCreateDto dto = UserFactory.createDemoMemberDto();

        assertThat(dto.email()).isEqualTo("member@portfolio.example.com");
        assertThat(dto.firstName()).isEqualTo("Taro");
        assertThat(dto.lastName()).isEqualTo("Yamada");
    }

    @Test
    @DisplayName("Should create demo guest DTO")
    void shouldCreateDemoGuestDto() {
        UserFactory.UserCreateDto dto = UserFactory.createDemoGuestDto(
            "session-123",
            "192.168.1.1",
            "Mozilla/5.0"
        );

        assertThat(dto.sessionId()).isEqualTo("session-123");
        assertThat(dto.ipAddress()).isEqualTo("192.168.1.1");
        assertThat(dto.userAgent()).isEqualTo("Mozilla/5.0");
    }
}
