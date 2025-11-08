package com.sebsrvv.app.modules.auth.exception;

public class DeleteAccountException extends RuntimeException {
    public DeleteAccountException(String message) {
        super(message);
    }

    public DeleteAccountException(String message, Throwable cause) {
        super(message, cause);
    }
}
