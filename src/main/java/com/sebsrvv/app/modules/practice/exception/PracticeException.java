package com.sebsrvv.app.modules.practice.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;


public class PracticeException extends RuntimeException {
    private final HttpStatus status;
    private final String code;

     public PracticeException(HttpStatus status, String code, String message) {
        super(message);
        this.status = status;
        this.code = code;
    }

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }
}
