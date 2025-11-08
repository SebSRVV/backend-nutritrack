package com.sebsrvv.app.modules.practice.exception;

import org.springframework.http.HttpStatus;

public class NoValidDeleteException extends PracticeException {
    public NoValidDeleteException() {
        super(
                HttpStatus.BAD_REQUEST,
                "INVALID_DELETE_METHOD",
                "El metodo de eliminacion puesto no es valido. Debe ser o soft o hard"
        );
    }
}
