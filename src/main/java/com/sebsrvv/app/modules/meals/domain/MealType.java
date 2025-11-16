package com.sebsrvv.app.modules.meals.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK;

    @JsonCreator
    public static MealType fromString(String key) {
        if (key == null) return null;
        return MealType.valueOf(key.trim().toUpperCase());
    }

    @JsonValue
    public String toValue() {
        return this.name();
    }
}
