package com.sebsrvv.app.modules.practice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NoUserException extends PracticeException {

    public NoUserException(UUID userId) {
        super(
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND",
                String.format("No se encontró ningún usuario con el ID: %s", userId)
        );
    }
}