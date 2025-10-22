package com.sebsrvv.app.modules.auth.exception;

import org.springframework.http.HttpStatus;

public class UsernameAlreadyExistsException extends AuthException {
    public UsernameAlreadyExistsException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "username_already_exists", "El nombre de usuario ya se encuentra registrado");
    }
}
