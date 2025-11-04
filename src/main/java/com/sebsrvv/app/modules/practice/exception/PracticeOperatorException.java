package com.sebsrvv.app.modules.practice.exception;

import org.springframework.http.HttpStatus;

public class PracticeOperatorException extends PracticeException {
    public PracticeOperatorException() {
        super(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "INVALID_OPERATOR",
                "El operador puesto no es aceptable. Debe ser entre estos valores: gte, lte, eq"
        );
    }
}
