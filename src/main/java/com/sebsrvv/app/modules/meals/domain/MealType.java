package com.sebsrvv.app.modules.meals.domain;

public enum MealType {
    BREAKFAST, LUNCH, DINNER, SNACK;

    public String toDbValue() {
        return name().toLowerCase();
    }

    public static MealType fromDbValue(String db) {
        return MealType.valueOf(db.toUpperCase());
    }
}
