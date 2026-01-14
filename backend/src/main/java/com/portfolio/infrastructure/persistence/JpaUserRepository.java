package com.portfolio.infrastructure.persistence;

import com.portfolio.domain.entity.User;
import com.portfolio.domain.repository.UserRepository;
import com.portfolio.domain.valueobject.Email;
import com.portfolio.domain.valueobject.UserId;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * JPA implementation of UserRepository
 * Demonstrates Repository Pattern adapter for persistence layer
 */
@Repository
@Transactional
public class JpaUserRepository implements UserRepository {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Find user by ID
     * @param id - user ID
     * @return Optional - user if found
     */
    @Override
    public Optional<User> findById(UserId id) {
        User user = entityManager.find(User.class, id.getValue().toString());
        return Optional.ofNullable(user);
    }

    /**
     * Find user by email
     * @param email - email address
     * @return Optional - user if found
     */
    @Override
    public Optional<User> findByEmail(Email email) {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email", email.getValue());

        List<User> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find user by username
     * @param username - username
     * @return Optional - user if found
     */
    @Override
    public Optional<User> findByUsername(String username) {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username", username);

        List<User> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Find all users by type
     * @param userType - user type discriminator
     * @return List - matching users
     */
    @Override
    public List<User> findByUserType(String userType) {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u WHERE TYPE(u) = :type", User.class);

        Class<? extends User> typeClass = switch (userType.toUpperCase()) {
            case "ADMIN" -> com.portfolio.domain.entity.Admin.class;
            case "MEMBER" -> com.portfolio.domain.entity.Member.class;
            case "GUEST" -> com.portfolio.domain.entity.Guest.class;
            default -> throw new IllegalArgumentException("Unknown user type: " + userType);
        };

        query.setParameter("type", typeClass);
        return query.getResultList();
    }

    /**
     * Find all active users
     * @return List - active users
     */
    @Override
    public List<User> findAllActive() {
        TypedQuery<User> query = entityManager.createQuery(
            "SELECT u FROM User u WHERE u.isActive = true", User.class);
        return query.getResultList();
    }

    /**
     * Save user
     * @param user - user to save
     * @return User - saved user
     */
    @Override
    public User save(User user) {
        if (findById(user.getId()).isEmpty()) {
            entityManager.persist(user);
            return user;
        } else {
            return entityManager.merge(user);
        }
    }

    /**
     * Delete user
     * @param user - user to delete
     */
    @Override
    public void delete(User user) {
        entityManager.remove(entityManager.contains(user) ? user : entityManager.merge(user));
    }

    /**
     * Delete user by ID
     * @param id - user ID
     */
    @Override
    public void deleteById(UserId id) {
        findById(id).ifPresent(this::delete);
    }

    /**
     * Check if user exists by email
     * @param email - email to check
     * @return boolean - true if exists
     */
    @Override
    public boolean existsByEmail(Email email) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.email = :email", Long.class);
        query.setParameter("email", email.getValue());
        return query.getSingleResult() > 0;
    }

    /**
     * Check if user exists by username
     * @param username - username to check
     * @return boolean - true if exists
     */
    @Override
    public boolean existsByUsername(String username) {
        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM User u WHERE u.username = :username", Long.class);
        query.setParameter("username", username);
        return query.getSingleResult() > 0;
    }

    /**
     * Count users by type
     * @param userType - user type
     * @return long - count
     */
    @Override
    public long countByUserType(String userType) {
        Class<? extends User> typeClass = switch (userType.toUpperCase()) {
            case "ADMIN" -> com.portfolio.domain.entity.Admin.class;
            case "MEMBER" -> com.portfolio.domain.entity.Member.class;
            case "GUEST" -> com.portfolio.domain.entity.Guest.class;
            default -> throw new IllegalArgumentException("Unknown user type: " + userType);
        };

        TypedQuery<Long> query = entityManager.createQuery(
            "SELECT COUNT(u) FROM User u WHERE TYPE(u) = :type", Long.class);
        query.setParameter("type", typeClass);
        return query.getSingleResult();
    }
}
