package com.sebsrvv.app.modules.auth.exception;


public class UsernameAlreadyExistsException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UsernameAlreadyExistsException() {
        super("El nombre de usuario ya está en uso");
    }

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }

    public UsernameAlreadyExistsException(String message, Throwable cause) {
        super(message, cause);
    }

    public UsernameAlreadyExistsException(Throwable cause) {
        super(cause);
    }
}