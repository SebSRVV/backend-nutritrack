package com.sebsrvv.app.modules.practice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NoPracticeException extends PracticeException {
    public NoPracticeException(UUID practiceId) {
        super(
                HttpStatus.NOT_FOUND,
                "PRACTICE_NOT_FOUND",
                String.format("No se encontró ninguna practica con el ID: %s", practiceId)
        );
    }
}
