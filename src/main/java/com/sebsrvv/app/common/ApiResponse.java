// src/main/java/com/sebsrvv/app/common/ApiResponse.java
package com.sebsrvv.app.common;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(
        boolean ok,
        T data,
        String message,
        Integer status,
        String timestamp
) {
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(true, data, null, 200, Instant.now().toString());
    }

    public static <T> ApiResponse<T> ok(T data, String message) {
        return new ApiResponse<>(true, data, message, 200, Instant.now().toString());
    }

    public static <T> ApiResponse<T> created(T data) {
        return new ApiResponse<>(true, data, null, 201, Instant.now().toString());
    }
}
