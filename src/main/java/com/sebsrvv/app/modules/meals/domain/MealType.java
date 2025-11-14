// src/main/java/com/sebsrvv/app/modules/meals/domain/MealType.java
package com.sebsrvv.app.modules.meals.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Tipos de comida. Soporta deserialización case-insensitive desde JSON
 * (por ejemplo "breakfast" -> BREAKFAST).
 */
public enum MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK;

    @JsonCreator
    public static MealType from(String value) {
        if (value == null) return null;
        try {
            return MealType.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException ex) {
            // rethrow with a clearer message (puede atraparse en ApiExceptionHandler)
            throw new IllegalArgumentException("Tipo de comida inválido: " + value);
        }
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
