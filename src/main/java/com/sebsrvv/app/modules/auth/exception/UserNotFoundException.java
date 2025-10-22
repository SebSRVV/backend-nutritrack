package com.sebsrvv.app.modules.auth.exception;

import org.springframework.http.HttpStatus;

public class UserNotFoundException extends AuthException {
    public UserNotFoundException() {
        super(HttpStatus.NOT_FOUND, "user_not_found", "No se encontro ningun usuario con las credenciales proporcionadas");
    }
}