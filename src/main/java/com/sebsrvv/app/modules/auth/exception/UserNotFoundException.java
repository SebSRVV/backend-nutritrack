package com.sebsrvv.app.modules.auth.exception;

public class UserNotFoundException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public UserNotFoundException() {
        super("Usuario no encontrado");
    }

    public UserNotFoundException(String message) {
        super(message);
    }

    public UserNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public UserNotFoundException(Throwable cause) {
        super(cause);
    }
}