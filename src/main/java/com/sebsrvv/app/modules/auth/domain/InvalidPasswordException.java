// src/main/java/com/sebsrvv/app/modules/auth/domain/InvalidPasswordException.java
package com.sebsrvv.app.modules.auth.domain;

public class InvalidPasswordException extends RuntimeException {
    public InvalidPasswordException(String msg) { super(msg); }
}