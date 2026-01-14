package com.portfolio.application.port.output;

import java.util.List;
import java.util.Optional;

/**
 * Base repository interface (output port) for domain entities
 * @param <T> Entity type
 * @param <ID> Entity identifier type
 */
public interface Repository<T, ID> {

    /**
     * Save an entity
     * @param entity Entity to save
     * @return Saved entity
     */
    T save(T entity);

    /**
     * Save multiple entities
     * @param entityList Entities to save
     * @return Saved entities
     */
    List<T> saveAll(List<T> entityList);

    /**
     * Find entity by identifier
     * @param id Entity identifier
     * @return Optional containing entity if found
     */
    Optional<T> findById(ID id);

    /**
     * Find all entities
     * @return List of all entities
     */
    List<T> findAll();

    /**
     * Check if entity exists by identifier
     * @param id Entity identifier
     * @return true if entity exists
     */
    boolean existsById(ID id);

    /**
     * Delete entity by identifier
     * @param id Entity identifier
     */
    void deleteById(ID id);

    /**
     * Delete an entity
     * @param entity Entity to delete
     */
    void delete(T entity);

    /**
     * Count total entities
     * @return Total count
     */
    long count();
}
