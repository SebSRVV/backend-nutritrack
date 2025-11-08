package com.sebsrvv.app.modules.meals.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;
/**
 * Excepción personalizada para representar errores de solicitud inválida (400 Bad Request).
 *
 * Se lanza cuando el cliente envía datos incorrectos o mal formados.

 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class BadRequestException extends RuntimeException {
    public BadRequestException(String message) {
        super(message);
    }
}