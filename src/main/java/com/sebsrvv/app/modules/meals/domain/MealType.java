package com.sebsrvv.app.modules.meals.domain;

import java.util.Arrays;

public enum MealType {
    breakfast,
    lunch,
    dinner,
    snack,
    other;

    public static MealType fromString(String raw) {
        return Arrays.stream(values())
                .filter(v -> v.name().equalsIgnoreCase(raw))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("mealType inválido: " + raw));
    }

    public String dbValue() {
        return name(); // por si algún día quieres usar el string directo
    }
}
