package com.sebsrvv.app.modules.meals.exception;

import java.util.UUID;

public class MealNotFoundException extends MealException {
    public MealNotFoundException(UUID id) {
        super("Meal no encontrado con id: " + id);
    }
}
