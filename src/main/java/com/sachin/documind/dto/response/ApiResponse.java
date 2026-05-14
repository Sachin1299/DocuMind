package com.sachin.documind.dto.response;

import java.time.LocalDateTime;

public record ApiResponse<T>(
    int status,
    String message,
    T data,
    String error,
    LocalDateTime timestamp
) {
    public static <T> ApiResponse<T> success(T data, String message) {
        return new ApiResponse<>(200, message, data, null, LocalDateTime.now());
    }

    public static <T> ApiResponse<T> error(int status, String error, String message) {
        return new ApiResponse<>(status, message, null, error, LocalDateTime.now());
    }
}
