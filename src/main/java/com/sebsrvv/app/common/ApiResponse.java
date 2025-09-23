// common/ApiResponse.java
package com.sebsrvv.app.common;
public record ApiResponse<T>(boolean ok, T data) {
    public static <T> ApiResponse<T> ok(T data) { return new ApiResponse<>(true, data); }
}
