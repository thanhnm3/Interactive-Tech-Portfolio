package com.portfolio.domain.entity;

import com.portfolio.domain.valueobject.Email;
import com.portfolio.domain.valueobject.UserId;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class for User entity using Single Table Inheritance
 * Demonstrates inheritance pattern with discriminator column
 */
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

    @Column(name = "username", nullable = false, unique = true)
    private String username;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Default constructor for JPA
     */
    protected User() {
    }

    /**
     * Protected constructor for subclasses
     * @param id - user identifier
     * @param email - email address
     * @param username - username
     * @param passwordHash - hashed password
     */
    protected User(UserId id, Email email, String username, String passwordHash) {
        this.id = id.getValue().toString();
        this.email = email.getValue();
        this.username = Objects.requireNonNull(username, "Username cannot be null");
        this.passwordHash = Objects.requireNonNull(passwordHash, "Password hash cannot be null");
        this.isActive = true;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * Get user ID as value object
     * @return UserId - user identifier
     */
    public UserId getId() {
        return UserId.of(id);
    }

    /**
     * Get email as value object
     * @return Email - email address
     */
    public Email getEmail() {
        return Email.of(email);
    }

    /**
     * Get username
     * @return String - username
     */
    public String getUsername() {
        return username;
    }

    /**
     * Get password hash for verification
     * @return String - hashed password
     */
    public String getPasswordHash() {
        return passwordHash;
    }

    /**
     * Check if user is active
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
     * Get the user type/role
     * @return String - user type discriminator
     */
    public abstract String getUserType();

    /**
     * Get display name for the user
     * @return String - display name
     */
    public abstract String getDisplayName();

    /**
     * Check if user has admin privileges
     * @return boolean - true if admin
     */
    public abstract boolean hasAdminPrivileges();

    /**
     * Activate the user account
     */
    public void activate() {
        this.isActive = true;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Deactivate the user account
     */
    public void deactivate() {
        this.isActive = false;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update email address
     * @param newEmail - new email
     */
    public void updateEmail(Email newEmail) {
        this.email = newEmail.getValue();
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Update password hash
     * @param newPasswordHash - new hashed password
     */
    public void updatePassword(String newPasswordHash) {
        this.passwordHash = Objects.requireNonNull(newPasswordHash, "Password hash cannot be null");
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("%s[id=%s, username=%s, type=%s]",
            getClass().getSimpleName(), id, username, getUserType());
    }
}
