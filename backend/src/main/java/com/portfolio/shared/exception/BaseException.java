package com.portfolio.shared.exception;

import lombok.Getter;

/**
 * Base exception class for all application exceptions
 */
@Getter
public abstract class BaseException extends RuntimeException {

    private final String errorCode;
    private final String errorMessage;

    /**
     * Constructor with error code and message
     * @param errorCode Error code identifier
     * @param errorMessage Human readable error message
     */
    protected BaseException(String errorCode, String errorMessage) {
        super(errorMessage);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }

    /**
     * Constructor with error code, message and cause
     * @param errorCode Error code identifier
     * @param errorMessage Human readable error message
     * @param cause The root cause exception
     */
    protected BaseException(String errorCode, String errorMessage, Throwable cause) {
        super(errorMessage, cause);
        this.errorCode = errorCode;
        this.errorMessage = errorMessage;
    }
}
