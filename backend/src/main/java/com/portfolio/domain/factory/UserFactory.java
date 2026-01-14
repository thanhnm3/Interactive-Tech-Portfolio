package com.portfolio.domain.factory;

import com.portfolio.domain.entity.Admin;
import com.portfolio.domain.entity.Guest;
import com.portfolio.domain.entity.Member;
import com.portfolio.domain.entity.User;
import com.portfolio.domain.valueobject.Email;
import com.portfolio.domain.valueobject.UserId;
import org.springframework.stereotype.Component;

/**
 * Factory for creating User entities
 * Demonstrates Factory Pattern for polymorphic object creation
 */
@Component
public class UserFactory {

    /**
     * User type enumeration
     */
    public enum UserType {
        ADMIN,
        MEMBER,
        GUEST
    }

    /**
     * DTO for user creation data
     */
    public record UserCreateDto(
        String email,
        String username,
        String passwordHash,
        String firstName,
        String lastName,
        String department,
        Integer adminLevel,
        String sessionId,
        String ipAddress,
        String userAgent
    ) {}

    /**
     * Create user based on type
     * @param type - user type
     * @param dto - creation data
     * @return User - created user instance
     */
    public User createUser(UserType type, UserCreateDto dto) {
        return switch (type) {
            case ADMIN -> createAdmin(dto);
            case MEMBER -> createMember(dto);
            case GUEST -> createGuest(dto);
        };
    }

    /**
     * Create user from string type
     * @param type - user type string
     * @param dto - creation data
     * @return User - created user instance
     */
    public User createUser(String type, UserCreateDto dto) {
        UserType userType = UserType.valueOf(type.toUpperCase());
        return createUser(userType, dto);
    }

    /**
     * Create Admin user
     * @param dto - creation data
     * @return Admin - created admin
     */
    public Admin createAdmin(UserCreateDto dto) {
        validateAdminData(dto);

        return new Admin(
            UserId.generate(),
            Email.of(dto.email()),
            dto.username(),
            dto.passwordHash(),
            dto.department(),
            dto.adminLevel()
        );
    }

    /**
     * Create Member user
     * @param dto - creation data
     * @return Member - created member
     */
    public Member createMember(UserCreateDto dto) {
        validateMemberData(dto);

        return new Member(
            UserId.generate(),
            Email.of(dto.email()),
            dto.username(),
            dto.passwordHash(),
            dto.firstName(),
            dto.lastName()
        );
    }

    /**
     * Create Guest user
     * @param dto - creation data
     * @return Guest - created guest
     */
    public Guest createGuest(UserCreateDto dto) {
        validateGuestData(dto);

        return new Guest(
            UserId.generate(),
            dto.sessionId(),
            dto.ipAddress(),
            dto.userAgent()
        );
    }

    /**
     * Validate admin creation data
     * @param dto - creation data
     */
    private void validateAdminData(UserCreateDto dto) {
        if (dto.email() == null || dto.email().isBlank()) {
            throw new IllegalArgumentException("Email is required for Admin");
        }

        if (dto.username() == null || dto.username().isBlank()) {
            throw new IllegalArgumentException("Username is required for Admin");
        }

        if (dto.passwordHash() == null || dto.passwordHash().isBlank()) {
            throw new IllegalArgumentException("Password is required for Admin");
        }

        if (dto.adminLevel() == null || dto.adminLevel() < 1 || dto.adminLevel() > 5) {
            throw new IllegalArgumentException("Admin level must be between 1 and 5");
        }
    }

    /**
     * Validate member creation data
     * @param dto - creation data
     */
    private void validateMemberData(UserCreateDto dto) {
        if (dto.email() == null || dto.email().isBlank()) {
            throw new IllegalArgumentException("Email is required for Member");
        }

        if (dto.username() == null || dto.username().isBlank()) {
            throw new IllegalArgumentException("Username is required for Member");
        }

        if (dto.passwordHash() == null || dto.passwordHash().isBlank()) {
            throw new IllegalArgumentException("Password is required for Member");
        }
    }

    /**
     * Validate guest creation data
     * @param dto - creation data
     */
    private void validateGuestData(UserCreateDto dto) {
        if (dto.sessionId() == null || dto.sessionId().isBlank()) {
            throw new IllegalArgumentException("Session ID is required for Guest");
        }
    }

    /**
     * Create demo users for testing
     * @return UserCreateDto - admin demo data
     */
    public static UserCreateDto createDemoAdminDto() {
        return new UserCreateDto(
            "admin@portfolio.example.com",
            "admin_user",
            "hashed_password_here",
            null,
            null,
            "Engineering",
            3,
            null,
            null,
            null
        );
    }

    /**
     * Create demo member data
     * @return UserCreateDto - member demo data
     */
    public static UserCreateDto createDemoMemberDto() {
        return new UserCreateDto(
            "member@portfolio.example.com",
            "member_user",
            "hashed_password_here",
            "Taro",
            "Yamada",
            null,
            null,
            null,
            null,
            null
        );
    }

    /**
     * Create demo guest data
     * @param sessionId - session ID
     * @param ipAddress - IP address
     * @param userAgent - user agent
     * @return UserCreateDto - guest demo data
     */
    public static UserCreateDto createDemoGuestDto(String sessionId, String ipAddress, String userAgent) {
        return new UserCreateDto(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            sessionId,
            ipAddress,
            userAgent
        );
    }
}
