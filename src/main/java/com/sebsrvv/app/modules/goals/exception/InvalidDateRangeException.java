package com.sebsrvv.app.modules.goals.exception;

import org.springframework.http.HttpStatus;

public class InvalidDateRangeException extends GoalException {
    public InvalidDateRangeException() {
        super(
                HttpStatus.BAD_REQUEST,
                "DATE_RANGE_INVALID",
                "start_date debe ser anterior o igual a end_date"
        );
    }
}
