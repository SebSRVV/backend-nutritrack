package com.sebsrvv.app.modules.goals.exception;

import org.springframework.http.HttpStatus;

public class InvalidWeeklyTargetException extends GoalException {
    public InvalidWeeklyTargetException() {
        super(
                HttpStatus.BAD_REQUEST,
                "WEEKLY_TARGET_RANGE",
                "El weekly_target debe estar en el rango 1..7"
        );
    }
}
