package com.sebsrvv.app.modules.auth.exception;

public class AuthException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public AuthException() {
        super("Error de autenticación");
    }

    public AuthException(String message) {
        super(message);
    }

    public AuthException(String message, Throwable cause) {
        super(message, cause);
    }

    public AuthException(Throwable cause) {
        super(cause);
    }
}

