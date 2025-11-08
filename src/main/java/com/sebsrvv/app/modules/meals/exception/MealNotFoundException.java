package com.sebsrvv.app.modules.meals.exception;

/**
 * Excepción personalizada que se lanza cuando no se encuentra
 * un registro de comida (Meal) en la base de datos.
 *
 * Por ejemplo: cuando se intenta actualizar o eliminar un meal
 * que no existe para el ID proporcionado.

 */
public class MealNotFoundException extends RuntimeException {
    public MealNotFoundException(String message) {
        super(message);
    }
}
