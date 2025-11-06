package com.sebsrvv.app.modules.meals.exception;
//Se lanza cuando algo no existe en la base de datos (por ejemplo, un meal no encontrado).
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}

