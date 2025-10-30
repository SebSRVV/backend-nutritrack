package com.sebsrvv.app.modules.practice.exception;

import org.springframework.http.HttpStatus;

public class PracticesExceptions extends RuntimeException {
    private HttpStatus status;
    private String code;
    //public PracticesExceptions(String message) {
        //super(message);
    //}

    protected PracticesExceptions(String message, HttpStatus status, String code) {
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
