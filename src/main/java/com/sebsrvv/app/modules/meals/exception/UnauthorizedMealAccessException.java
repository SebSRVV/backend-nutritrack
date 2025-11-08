package com.sebsrvv.app.modules.meals.exception;

/**
 * Excepción personalizada que se lanza cuando un usuario intenta
 * acceder, modificar o eliminar una comida (Meal) que no le pertenece.

 */
public class UnauthorizedMealAccessException extends RuntimeException {


    public UnauthorizedMealAccessException(String message) {
        super(message);
    }
}