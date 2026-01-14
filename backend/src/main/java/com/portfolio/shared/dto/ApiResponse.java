package com.portfolio.shared.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

/**
 * Standard API response wrapper
 * @param <T> Response data type
 */
@Getter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private final boolean isSuccess;
    private final String message;
    private final T data;
    private final String errorCode;
    private final Object errorDetailList;
    @Builder.Default
    private final LocalDateTime timestamp = LocalDateTime.now();

    /**
     * Create success response with data
     * @param data Response data
     * @param <T> Data type
     * @return Success response
     */
    public static <T> ApiResponse<T> success(T data) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .message("Success")
                .data(data)
                .build();
    }

    /**
     * Create success response with message
     * @param message Success message
     * @param data Response data
     * @param <T> Data type
     * @return Success response
     */
    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .message(message)
                .data(data)
                .build();
    }

    /**
     * Create error response
     * @param errorCode Error code
     * @param message Error message
     * @param <T> Data type
     * @return Error response
     */
    public static <T> ApiResponse<T> error(String errorCode, String message) {
        return ApiResponse.<T>builder()
                .isSuccess(false)
                .errorCode(errorCode)
                .message(message)
                .build();
    }

    /**
     * Create error response with details
     * @param errorCode Error code
     * @param message Error message
     * @param errorDetailList Error details
     * @param <T> Data type
     * @return Error response
     */
    public static <T> ApiResponse<T> error(String errorCode, String message, Object errorDetailList) {
        return ApiResponse.<T>builder()
                .isSuccess(false)
                .errorCode(errorCode)
                .message(message)
                .errorDetailList(errorDetailList)
                .build();
    }
}
