package com.sebsrvv.app.modules.auth.exception;

import org.springframework.http.HttpStatus;

public class InvalidPasswordException extends AuthException {
    public InvalidPasswordException() {
        super(HttpStatus.BAD_REQUEST, "invalid_password", "La contrasena no cumple con los requisitos de seguridad");
    }
}