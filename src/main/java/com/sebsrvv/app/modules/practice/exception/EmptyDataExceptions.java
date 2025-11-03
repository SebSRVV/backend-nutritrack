package com.sebsrvv.app.modules.practice.exception;

import org.springframework.http.HttpStatus;

public class EmptyDataExceptions extends PracticeException {
    public EmptyDataExceptions() {
        super(
                HttpStatus.BAD_REQUEST,
                "EMPTY_DATA",
                "El cuerpo mandado se encuentra con campos vacios"
        );
    }
}
