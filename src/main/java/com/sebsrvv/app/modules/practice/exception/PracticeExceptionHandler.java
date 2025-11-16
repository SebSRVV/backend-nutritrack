package com.sebsrvv.app.modules.practice.exception;

import com.sebsrvv.app.modules.practice.exception.ErrorResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class PracticeExceptionHandler {

    @ExceptionHandler(PracticeException.class)
    public ResponseEntity<ErrorResponse> handlePracticeException(PracticeException ex) {
        ErrorResponse errorResponse = new ErrorResponse(ex.getCode(), ex.getMessage());

        return ResponseEntity
                .status(ex.getStatus())
                .body(errorResponse);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        ErrorResponse errorResponse = new ErrorResponse(
                "INTERNAL_SERVER_ERROR",
                "Ha ocurrido un error inesperado"
        );

        return ResponseEntity
                .status(500)
                .body(errorResponse);
    }
}
