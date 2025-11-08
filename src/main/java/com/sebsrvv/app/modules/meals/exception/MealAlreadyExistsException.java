package com.sebsrvv.app.modules.meals.exception;

public class MealAlreadyExistsException extends RuntimeException {
    public MealAlreadyExistsException(String message) {
        super(message);
    }
}
