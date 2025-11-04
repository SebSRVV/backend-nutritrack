package com.sebsrvv.app.modules.practice.exception;

import org.springframework.http.HttpStatus;

import java.util.UUID;

public class NoEntryFoundException extends PracticeException {
    public NoEntryFoundException(UUID entryId) {
        super(
                HttpStatus.BAD_REQUEST,
                "NO_ENTRY_FOUND",
                String.format("No se encontró ninguna entrada con el ID: %s", entryId)
        );
    }
}
