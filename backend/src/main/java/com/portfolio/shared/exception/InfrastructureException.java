package com.portfolio.shared.exception;

/**
 * Exception for infrastructure-level failures (database, external services, etc.)
 */
public class InfrastructureException extends BaseException {

    private static final String ERROR_CODE = "INFRASTRUCTURE_ERROR";

    /**
     * Constructor with message
     * @param message Error message
     */
    public InfrastructureException(String message) {
        super(ERROR_CODE, message);
    }

    /**
     * Constructor with message and cause
     * @param message Error message
     * @param cause The root cause exception
     */
    public InfrastructureException(String message, Throwable cause) {
        super(ERROR_CODE, message, cause);
    }
}
