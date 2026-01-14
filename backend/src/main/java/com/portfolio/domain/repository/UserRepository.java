package com.portfolio.domain.repository;

import com.portfolio.domain.entity.User;
import com.portfolio.domain.valueobject.Email;
import com.portfolio.domain.valueobject.UserId;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for User aggregate
 * Demonstrates Repository Pattern for domain persistence abstraction
 */
public interface UserRepository {

    /**
     * Find user by ID
     * @param id - user ID
     * @return Optional - user if found
     */
    Optional<User> findById(UserId id);

    /**
     * Find user by email
     * @param email - email address
     * @return Optional - user if found
     */
    Optional<User> findByEmail(Email email);

    /**
     * Find user by username
     * @param username - username
     * @return Optional - user if found
     */
    Optional<User> findByUsername(String username);

    /**
     * Find all users by type
     * @param userType - user type discriminator
     * @return List - matching users
     */
    List<User> findByUserType(String userType);

    /**
     * Find all active users
     * @return List - active users
     */
    List<User> findAllActive();

    /**
     * Save user (create or update)
     * @param user - user to save
     * @return User - saved user
     */
    User save(User user);

    /**
     * Delete user
     * @param user - user to delete
     */
    void delete(User user);

    /**
     * Delete user by ID
     * @param id - user ID
     */
    void deleteById(UserId id);

    /**
     * Check if user exists by email
     * @param email - email to check
     * @return boolean - true if exists
     */
    boolean existsByEmail(Email email);

    /**
     * Check if user exists by username
     * @param username - username to check
     * @return boolean - true if exists
     */
    boolean existsByUsername(String username);

    /**
     * Count users by type
     * @param userType - user type
     * @return long - count
     */
    long countByUserType(String userType);
}
