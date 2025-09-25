package com.sebsrvv.app.modules.auth.domain;

public class InvalidCredentialsException extends RuntimeException {
    public InvalidCredentialsException(String message) { super(message); }
}
