package com.portfolio.shared.exception;

/**
 * Exception for entity not found errors
 * Used when requested entity does not exist
 */
public class EntityNotFoundException extends DomainException {

    private final String entityType;
    private final String entityId;

    /**
     * Create entity not found exception
     * @param entityType - type of entity
     * @param entityId - entity identifier
     */
    public EntityNotFoundException(String entityType, String entityId) {
        super(String.format("%s with ID '%s' not found", entityType, entityId), "ENTITY_NOT_FOUND");
        this.entityType = entityType;
        this.entityId = entityId;
    }

    /**
     * Create entity not found exception with custom message
     * @param entityType - type of entity
     * @param entityId - entity identifier
     * @param message - custom message
     */
    public EntityNotFoundException(String entityType, String entityId, String message) {
        super(message, "ENTITY_NOT_FOUND");
        this.entityType = entityType;
        this.entityId = entityId;
    }

    /**
     * Get entity type
     * @return String - entity type
     */
    public String getEntityType() {
        return entityType;
    }

    /**
     * Get entity ID
     * @return String - entity ID
     */
    public String getEntityId() {
        return entityId;
    }
}
