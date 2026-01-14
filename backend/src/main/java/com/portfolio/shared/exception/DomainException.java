package com.portfolio.shared.exception;

/**
 * Base exception for domain layer errors
 * Extends RuntimeException for unchecked exception handling
 */
public class DomainException extends RuntimeException {

    private final String errorCode;

    /**
     * Create domain exception with message
     * @param message - error message
     */
    public DomainException(String message) {
        super(message);
        this.errorCode = "DOMAIN_ERROR";
    }

    /**
     * Create domain exception with message and code
     * @param message - error message
     * @param errorCode - error code
     */
    public DomainException(String message, String errorCode) {
        super(message);
        this.errorCode = errorCode;
    }

    /**
     * Create domain exception with message and cause
     * @param message - error message
     * @param cause - root cause
     */
    public DomainException(String message, Throwable cause) {
        super(message, cause);
        this.errorCode = "DOMAIN_ERROR";
    }

    /**
     * Create domain exception with message, code, and cause
     * @param message - error message
     * @param errorCode - error code
     * @param cause - root cause
     */
    public DomainException(String message, String errorCode, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    /**
     * Get error code
     * @return String - error code
     */
    public String getErrorCode() {
        return errorCode;
    }
}
