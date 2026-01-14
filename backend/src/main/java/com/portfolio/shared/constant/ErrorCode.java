package com.portfolio.shared.constant;

/**
 * Application error codes
 */
public final class ErrorCode {

    private ErrorCode() {
        // Prevent instantiation
    }

    // General errors
    public static final String INTERNAL_ERROR = "INTERNAL_ERROR";
    public static final String VALIDATION_ERROR = "VALIDATION_ERROR";
    public static final String ENTITY_NOT_FOUND = "ENTITY_NOT_FOUND";
    public static final String DUPLICATE_ENTITY = "DUPLICATE_ENTITY";
    public static final String UNAUTHORIZED = "UNAUTHORIZED";
    public static final String FORBIDDEN = "FORBIDDEN";

    // Business errors
    public static final String INSUFFICIENT_STOCK = "INSUFFICIENT_STOCK";
    public static final String INVALID_ORDER_STATUS = "INVALID_ORDER_STATUS";
    public static final String PAYMENT_FAILED = "PAYMENT_FAILED";
    public static final String ORDER_CANCELLED = "ORDER_CANCELLED";

    // Infrastructure errors
    public static final String DATABASE_ERROR = "DATABASE_ERROR";
    public static final String CACHE_ERROR = "CACHE_ERROR";
    public static final String MESSAGE_QUEUE_ERROR = "MESSAGE_QUEUE_ERROR";
    public static final String EXTERNAL_SERVICE_ERROR = "EXTERNAL_SERVICE_ERROR";
}
