package com.sebsrvv.app.modules.meals.exception;

public class UnauthorizedMealAccessException extends RuntimeException {
    public UnauthorizedMealAccessException(String message) {
        super(message);
    }
}
