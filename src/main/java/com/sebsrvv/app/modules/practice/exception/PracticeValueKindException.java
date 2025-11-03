package com.sebsrvv.app.modules.practice.exception;

import org.springframework.http.HttpStatus;

public class PracticeValueKindException extends PracticeException {
    public PracticeValueKindException(String message) {
        super(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INVALID_VALUE_KINND",
                "El value kind incertado no es valido. Se debe insertar uno de los siguietes valores: boolean, quantity"
        );
    }
}
