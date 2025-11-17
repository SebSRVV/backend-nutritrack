package com.sebsrvv.app.modules.meals.exception;

public class FoodCategoryNotFoundException extends MealException {
    public FoodCategoryNotFoundException() {
        super("Alguna categoría seleccionada no existe");
    }
}