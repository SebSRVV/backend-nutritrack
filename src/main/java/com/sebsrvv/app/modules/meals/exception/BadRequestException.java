package com.sebsrvv.app.modules.meals.exception;

public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}
//Se lanza cuando el usuario envía datos inválidos (campos vacíos, fechas erróneas, etc.).