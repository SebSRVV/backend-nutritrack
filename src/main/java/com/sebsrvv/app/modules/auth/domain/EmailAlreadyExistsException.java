// modules/auth/domain/EmailAlreadyExistsException.java
package com.sebsrvv.app.modules.auth.domain;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super("El email '" + email + "' ya está registrado");
    }
}
