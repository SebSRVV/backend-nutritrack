package com.sebsrvv.app.modules.auth.exception;

import org.springframework.http.HttpStatus;

public class EmailAlreadyExistsException extends AuthException {

    public EmailAlreadyExistsException() {
        super(HttpStatus.UNPROCESSABLE_ENTITY, "email_already_exists", "El correo ya se encuentra registrado");
    }
}
