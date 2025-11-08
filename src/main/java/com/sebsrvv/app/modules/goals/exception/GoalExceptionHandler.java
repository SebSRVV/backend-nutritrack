package com.sebsrvv.app.modules.goals.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.Map;

@RestControllerAdvice
public class GoalExceptionHandler {

    @ExceptionHandler(GoalException.class)
    public ResponseEntity<Map<String, Object>> handleGoalException(GoalException ex) {
        return ResponseEntity
                .status(ex.getStatus())
                .body(Map.of(
                        "error", ex.getCode(),
                        "message", ex.getDetail(),
                        "timestamp", OffsetDateTime.now().toString()
                ));
    }
}
