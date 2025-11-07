package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.web.dto.ErrorCode;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.util.NoSuchElementException;

@RestControllerAdvice
public class GlobalExceptionHandler {

    // Mapea validaciones de GOALS: usa el texto exacto del servicio para decidir el código
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorResponse> handleIllegalArgument(IllegalArgumentException ex, WebRequest req) {
        String msg = ex.getMessage() == null ? "" : ex.getMessage();
        ErrorCode code;

        if (msg.contains("Body vacío")) {
            code = ErrorCode.GOALS_EMPTY_BODY;
        } else if (msg.contains("weekly_target debe estar entre 1 y 7")) {
            code = ErrorCode.GOALS_INVALID_WEEKLY_TARGET;
        } else if (msg.contains("end_date no puede ser anterior a start_date")) {
            code = ErrorCode.GOALS_INVALID_DATE_RANGE;
        } else if (msg.contains("Valor inválido")) { // para PROGRESS_INVALID_VALUE
            code = ErrorCode.PROGRESS_INVALID_VALUE;
        } else {
            // por si aparece otro IllegalArgumentException no catalogado
            code = ErrorCode.GOALS_INVALID_DATE_RANGE;
        }

        ErrorResponse body = new ErrorResponse(code.code(), code.defaultMessage(), path(req));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    // Not found genérico (GOALS y PROGRESS)
    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ErrorResponse> handleNoSuchElement(NoSuchElementException ex, WebRequest req) {
        // Si el mensaje viene del servicio de Goals
        if (ex.getMessage() != null && ex.getMessage().contains("Meta no encontrada")) {
            ErrorResponse body = new ErrorResponse("GOALS_404", "Meta no encontrada.", path(req));
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
        }
        // Si viene de Progress
        ErrorResponse body = new ErrorResponse(ErrorCode.PROGRESS_NOT_FOUND.code(),
                ErrorCode.PROGRESS_NOT_FOUND.defaultMessage(), path(req));
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    // Duplicidad en progreso (si tu servicio de progreso la lanzara)
    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ErrorResponse> handleIllegalState(IllegalStateException ex, WebRequest req) {
        // Ej: “duplicado log_date”
        ErrorResponse body = new ErrorResponse(ErrorCode.PROGRESS_DUPLICATE_LOGDATE.code(),
                ErrorCode.PROGRESS_DUPLICATE_LOGDATE.defaultMessage(), path(req));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneric(Exception ex, WebRequest req) {
        ErrorResponse body = new ErrorResponse("GENERIC_500", "Error interno.", path(req));
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(body);
    }

    private String path(WebRequest req) {
        String desc = req.getDescription(false); // "uri=/api/goals..."
        return desc != null && desc.startsWith("uri=") ? desc.substring(4) : desc;
    }
}
