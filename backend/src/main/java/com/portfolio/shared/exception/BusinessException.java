package com.portfolio.shared.exception;

/**
 * Exception for business logic violations
 */
public class BusinessException extends BaseException {

    /**
     * Constructor with error code and message
     * @param errorCode Error code identifier
     * @param errorMessage Human readable error message
     */
    public BusinessException(String errorCode, String errorMessage) {
        super(errorCode, errorMessage);
    }

    /**
     * Constructor with error code, message and cause
     * @param errorCode Error code identifier
     * @param errorMessage Human readable error message
     * @param cause The root cause exception
     */
    public BusinessException(String errorCode, String errorMessage, Throwable cause) {
        super(errorCode, errorMessage, cause);
    }
}
