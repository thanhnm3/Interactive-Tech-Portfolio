package com.portfolio.infrastructure.web;

import com.portfolio.shared.exception.DomainException;
import com.portfolio.shared.exception.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Global exception handler for REST controllers
 * Provides consistent error response format
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Handle entity not found exceptions
     * @param ex - exception
     * @return ResponseEntity - error response
     */
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleEntityNotFoundException(EntityNotFoundException ex) {
        logger.warn("Entity not found: {} with ID {}", ex.getEntityType(), ex.getEntityId());

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(createErrorResponse(ex.getErrorCode(), ex.getMessage(), HttpStatus.NOT_FOUND));
    }

    /**
     * Handle domain exceptions
     * @param ex - exception
     * @return ResponseEntity - error response
     */
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<Map<String, Object>> handleDomainException(DomainException ex) {
        logger.error("Domain exception: {}", ex.getMessage(), ex);

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(createErrorResponse(ex.getErrorCode(), ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    /**
     * Handle illegal argument exceptions
     * @param ex - exception
     * @return ResponseEntity - error response
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgumentException(IllegalArgumentException ex) {
        logger.warn("Invalid argument: {}", ex.getMessage());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(createErrorResponse("INVALID_ARGUMENT", ex.getMessage(), HttpStatus.BAD_REQUEST));
    }

    /**
     * Handle general exceptions
     * @param ex - exception
     * @return ResponseEntity - error response
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneralException(Exception ex) {
        logger.error("Unexpected error occurred", ex);

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(createErrorResponse("INTERNAL_ERROR", "An unexpected error occurred", HttpStatus.INTERNAL_SERVER_ERROR));
    }

    /**
     * Create standardized error response
     * @param errorCode - error code
     * @param message - error message
     * @param status - HTTP status
     * @return Map - error response body
     */
    private Map<String, Object> createErrorResponse(String errorCode, String message, HttpStatus status) {
        return Map.of(
            "timestamp", LocalDateTime.now().toString(),
            "status", status.value(),
            "error", status.getReasonPhrase(),
            "code", errorCode,
            "message", message
        );
    }
}
