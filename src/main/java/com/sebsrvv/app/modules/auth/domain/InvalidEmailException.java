// src/main/java/com/sebsrvv/app/modules/auth/domain/InvalidEmailException.java
package com.sebsrvv.app.modules.auth.domain;

public class InvalidEmailException extends RuntimeException {
    public InvalidEmailException(String msg) { super(msg); }
}