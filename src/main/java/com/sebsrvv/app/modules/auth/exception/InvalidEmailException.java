package com.sebsrvv.app.modules.auth.exception;

import org.springframework.http.HttpStatus;

public class InvalidEmailException extends AuthException {
    public InvalidEmailException() {
        super(HttpStatus.BAD_REQUEST, "invalid_email", "El formato del correo no es valido");
    }
}