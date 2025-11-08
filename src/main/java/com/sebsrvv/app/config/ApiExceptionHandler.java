// src/main/java/com/sebsrvv/app/common/ApiExceptionHandler.java
package com.sebsrvv.app.config;

import com.sebsrvv.app.modules.auth.exception.EmailAlreadyExistsException;
import com.sebsrvv.app.modules.auth.exception.InvalidEmailException;
import com.sebsrvv.app.modules.auth.exception.InvalidPasswordException;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class ApiExceptionHandler {

    /* ----------- Validación DTOs ----------- */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "VALIDATION_ERROR");
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe ->
                details.put(fe.getField(), fe.getDefaultMessage()));
        body.put("message", "Hay campos inválidos.");
        body.put("details", details);
        return ResponseEntity.badRequest().body(body);
    }

    /* ----------- Reglas de negocio ----------- */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<Map<String, Object>> handleEmailDuplicated(EmailAlreadyExistsException ex) {
        Map<String, Object> body = baseBody(HttpStatus.CONFLICT, "EMAIL_ALREADY_EXISTS");
        body.put("message", safeMessage(ex, "El email ya está registrado."));
        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<Map<String, Object>> handleWeakPassword(InvalidPasswordException ex) {
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "INVALID_PASSWORD");
        body.put("message", safeMessage(ex, "La contraseña no cumple con los requisitos."));
        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<Map<String, Object>> handleInvalidEmail(InvalidEmailException ex) {
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "INVALID_EMAIL");
        body.put("message", safeMessage(ex, "El email no es válido."));
        return ResponseEntity.badRequest().body(body);
    }

    /* ----------- Errores HTTP del upstream ----------- */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<Map<String, Object>> handleWebClient(WebClientResponseException ex) {
        Map<String, Object> body = baseBody(ex.getStatusCode(), "UPSTREAM_ERROR");
        body.put("message", safeMessage(ex, "Error del servicio externo."));
        return ResponseEntity.status(ex.getStatusCode()).body(body);
    }

    /* ----------- Problemas de red / DNS / timeout ----------- */
    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<Map<String, Object>> handleWebClientRequest(WebClientRequestException ex) {
        Map<String, Object> body = baseBody(HttpStatus.BAD_GATEWAY, "UPSTREAM_UNREACHABLE");
        body.put("message", safeMessage(ex, "No se pudo contactar al servicio externo."));
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY).body(body);
    }

    /* ----------- Fallback ----------- */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
        Map<String, Object> body = baseBody(HttpStatus.BAD_REQUEST, "BAD_REQUEST");
        body.put("message", safeMessage(ex, "Se produjo un error inesperado."));
        return ResponseEntity.badRequest().body(body);
    }

    /* ---------- helpers ---------- */

    private String safeMessage(Throwable ex, String fallback) {
        if (ex == null) return fallback;
        String m = ex.getMessage();
        if (m != null && !m.isBlank()) return m;
        if (ex.getCause() != null) {
            String c = ex.getCause().getMessage();
            if (c != null && !c.isBlank()) return c;
        }
        return fallback + " (" + ex.getClass().getSimpleName() + ")";
    }

    private Map<String, Object> baseBody(HttpStatus status, String code) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", code);
        body.put("timestamp", Instant.now().toString());
        return body;
    }

    private Map<String, Object> baseBody(HttpStatusCode status, String code) {
        Map<String, Object> body = new HashMap<>();
        body.put("status", status.value());
        body.put("error", code);
        body.put("timestamp", Instant.now().toString());
        return body;
    }
}