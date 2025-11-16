package com.sebsrvv.app.modules.practice.exception;

import org.springframework.http.HttpStatus;

public class PracticeResponse extends PracticeException {
    public PracticeResponse(String code,String message) {
        super(HttpStatus.CREATED,
                code,message);
    }
}