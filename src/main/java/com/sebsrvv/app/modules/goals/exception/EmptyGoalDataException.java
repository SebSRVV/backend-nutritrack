package com.sebsrvv.app.modules.goals.exception;

import org.springframework.http.HttpStatus;

public class EmptyGoalDataException extends GoalException {
    public EmptyGoalDataException() {
        super(
                HttpStatus.BAD_REQUEST,
                "EMPTY_DATA",
                "El cuerpo enviado contiene campos vacíos o inválidos para la meta"
        );
    }
}
