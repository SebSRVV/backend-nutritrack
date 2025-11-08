package com.sebsrvv.app.modules.meals.exception;
/**
 * Excepción personalizada que se lanza cuando se intenta registrar
 * una comida (Meal) que ya existe en la base de datos para el mismo usuario y fecha.

 * Por ejemplo: si un usuario ya tiene registrada una comida "Desayuno" el mismo día.
 **/
public class MealAlreadyExistsException extends RuntimeException {
    public MealAlreadyExistsException(String message) {
        super(message);
    }
}
