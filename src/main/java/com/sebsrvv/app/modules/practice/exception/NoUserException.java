package com.sebsrvv.app.modules.practice.exception;

import com.sebsrvv.app.modules.auth.exception.AuthException;
import org.springframework.http.HttpStatus;

public class NoUserException extends PracticesExceptions {
    public NoUserException(String userId) {
        super(
                String.format("No se encontró ningún usuario con el ID: %s", userId),
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND"
        );
    }

    public NoUserException() {
        super(
                "No se encontró ningún usuario con las credenciales proporcionadas",
                HttpStatus.NOT_FOUND,
                "USER_NOT_FOUND"
        );
    }
}