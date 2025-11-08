package com.sebsrvv.app.modules.goals.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class GoalNotFoundException extends GoalException {
    public GoalNotFoundException(UUID goalId) {
        super(
                HttpStatus.NOT_FOUND,
                "GOAL_NOT_FOUND",
                "La meta solicitada no existe o no pertenece al usuario"
        );
    }
}
