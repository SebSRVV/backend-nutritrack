package com.sebsrvv.app.modules.meals.domain;

public enum MealType {
    BREAKFAST,
    LUNCH,
    DINNER,
    SNACK;

    public static MealType from(int value) {
        switch (value) {
            case 0: return BREAKFAST;
            case 1: return LUNCH;
            case 2: return DINNER;
            case 3: return SNACK;
            default: throw new IllegalArgumentException("Tipo de comida inválido: " + value);
        }
    }

    public int toValue() {
        return this.ordinal();
    }
}
