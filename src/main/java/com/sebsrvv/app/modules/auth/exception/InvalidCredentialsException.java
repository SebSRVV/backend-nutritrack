package com.sebsrvv.app.modules.auth.exception;

import org.springframework.http.HttpStatus;

public class InvalidCredentialsException extends AuthException {
    public InvalidCredentialsException() {
        super(HttpStatus.UNAUTHORIZED, "invalid_credentials", "Las credenciales proporcionadas no son validas");
    }
}