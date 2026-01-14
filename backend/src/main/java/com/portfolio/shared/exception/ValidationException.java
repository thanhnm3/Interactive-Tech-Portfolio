package com.portfolio.shared.exception;

import java.util.Map;
import lombok.Getter;

/**
 * Exception for input validation failures
 */
@Getter
public class ValidationException extends BaseException {

    private static final String ERROR_CODE = "VALIDATION_ERROR";
    private final Map<String, String> validationErrorMap;

    /**
     * Constructor with validation message
     * @param message Validation error message
     */
    public ValidationException(String message) {
        super(ERROR_CODE, message);
        this.validationErrorMap = Map.of();
    }

    /**
     * Constructor with field-specific validation errors
     * @param validationErrorMap Map of field names to error messages
     */
    public ValidationException(Map<String, String> validationErrorMap) {
        super(ERROR_CODE, "Validation failed for one or more fields");
        this.validationErrorMap = validationErrorMap;
    }
}
