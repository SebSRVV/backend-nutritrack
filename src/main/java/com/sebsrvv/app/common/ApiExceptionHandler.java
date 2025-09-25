package com.sebsrvv.app.common;

import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.sebsrvv.app.modules.auth.domain.EmailAlreadyExistsException;
import com.sebsrvv.app.modules.auth.domain.InvalidEmailException;
import com.sebsrvv.app.modules.auth.domain.InvalidPasswordException;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ServerWebInputException;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
@Order(0)
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    /* ----------- Validación DTOs (@Valid) ----------- */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> details = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(fe -> details.put(fe.getField(), fe.getDefaultMessage()));

        log.debug("[VALIDATION] {}", details);
        return ResponseEntity.badRequest().body(ApiResponse.fail(details, "Hay campos inválidos.", 400));
    }

    /* ----------- Violaciones (@Validated en params/path) ----------- */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleConstraint(ConstraintViolationException ex) {
        Map<String, String> details = ex.getConstraintViolations().stream()
                .collect(Collectors.toMap(
                        ApiExceptionHandler::violationPath,
                        ConstraintViolation::getMessage,
                        (a, b) -> a
                ));
        log.debug("[CONSTRAINT] {}", details);
        return ResponseEntity.badRequest().body(ApiResponse.fail(details, "Hay parámetros inválidos.", 400));
    }

    /* ----------- Errores de parseo/binding (Jackson / WebFlux) ----------- */
    @ExceptionHandler({
            HttpMessageNotReadableException.class,
            ServerWebInputException.class,
            MethodArgumentTypeMismatchException.class,
            InvalidFormatException.class
    })
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleBodyReadables(Exception ex) {
        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);

        Map<String, Object> details = new HashMap<>();
        details.put("exception", ex.getClass().getSimpleName());
        details.put("rootException", root != null ? root.getClass().getSimpleName() : null);
        details.put("rootMessage", root != null ? root.getMessage() : null);
        details.put("timestamp", Instant.now().toString());

        // Si es enum/valor inválido, intentar extraer el "path" del campo
        if (root instanceof InvalidFormatException ife) {
            details.put("targetType", ife.getTargetType() != null ? ife.getTargetType().getSimpleName() : null);
            details.put("value", ife.getValue());
            details.put("fieldPath", joinPath(ife.getPath()));
        } else if (ex instanceof ServerWebInputException swe && swe.getMethodParameter() != null) {
            details.put("parameter", swe.getMethodParameter().getParameterName());
        }

        log.debug("[BINDING] {} -> {}", ex.getClass().getSimpleName(), details);
        return ResponseEntity.badRequest().body(ApiResponse.fail(details, "Cuerpo de la solicitud inválido.", 400));
    }

    /* ----------- Reglas de negocio ----------- */
    @ExceptionHandler(EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<Void>> handleEmailDuplicated(EmailAlreadyExistsException ex) {
        log.info("[BUSINESS] {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(ApiResponse.fail(safeMessage(ex, "El email ya está registrado."), 409));
    }

    @ExceptionHandler(InvalidPasswordException.class)
    public ResponseEntity<ApiResponse<Void>> handleWeakPassword(InvalidPasswordException ex) {
        log.info("[BUSINESS] {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(safeMessage(ex, "La contraseña no cumple con los requisitos."), 400));
    }

    @ExceptionHandler(InvalidEmailException.class)
    public ResponseEntity<ApiResponse<Void>> handleInvalidEmail(InvalidEmailException ex) {
        log.info("[BUSINESS] {}", ex.getMessage());
        return ResponseEntity.badRequest()
                .body(ApiResponse.fail(safeMessage(ex, "El email no es válido."), 400));
    }

    /* ----------- Upstream HTTP (GoTrue / PostgREST) ----------- */
    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleWebClient(WebClientResponseException ex) {
        Map<String, Object> details = new HashMap<>();
        details.put("status", ex.getRawStatusCode());
        details.put("responseBody", truncate(ex.getResponseBodyAsString(), 2_000));
        details.put("headers", ex.getHeaders());
        log.warn("[UPSTREAM_ERROR] {} {}", ex.getRawStatusCode(), ex.getMessage());
        return ResponseEntity.status(ex.getStatusCode())
                .body(ApiResponse.fail(details, safeMessage(ex, "Error del servicio externo."), ex.getRawStatusCode()));
    }

    /* ----------- Problemas de red / DNS / timeout ----------- */
    @ExceptionHandler(WebClientRequestException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleWebClientRequest(WebClientRequestException ex) {
        Map<String, Object> details = new HashMap<>();
        details.put("cause", ex.getClass().getSimpleName());
        details.put("rootMessage", NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
        log.warn("[UPSTREAM_UNREACHABLE] {}", details);
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body(ApiResponse.fail(details, safeMessage(ex, "No se pudo contactar al servicio externo."), 502));
    }

    /* ----------- Fallback (incluye IllegalArgumentException de @JsonCreator) ----------- */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleIllegalArg(IllegalArgumentException ex) {
        // Muy útil para enums: mensajes como "activity_level inválido: X"
        Map<String, Object> details = Map.of(
                "exception", "IllegalArgumentException",
                "message", ex.getMessage()
        );
        log.debug("[ILLEGAL_ARGUMENT] {}", ex.getMessage());
        return ResponseEntity.badRequest().body(ApiResponse.fail(details, "Parámetro/valor inválido.", 400));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Map<String, Object>>> handleGeneric(Exception ex) {
        Map<String, Object> details = new HashMap<>();
        details.put("exception", ex.getClass().getName());
        details.put("rootMessage", NestedExceptionUtils.getMostSpecificCause(ex).getMessage());
        log.error("[UNCAUGHT] {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.fail(details, "Se produjo un error inesperado.", 500));
    }

    /* ---------- helpers ---------- */

    private static String safeMessage(Throwable ex, String fallback) {
        if (ex == null) return fallback;
        String m = ex.getMessage();
        if (StringUtils.hasText(m)) return m;
        Throwable root = NestedExceptionUtils.getMostSpecificCause(ex);
        if (root != null && StringUtils.hasText(root.getMessage())) return root.getMessage();
        return fallback + " (" + ex.getClass().getSimpleName() + ")";
    }

    private static String violationPath(ConstraintViolation<?> v) {
        String p = v.getPropertyPath() != null ? v.getPropertyPath().toString() : "";
        return StringUtils.hasText(p) ? p : "param";
    }

    private static String joinPath(java.util.List<JsonMappingException.Reference> path) {
        if (path == null || path.isEmpty()) return null;
        return path.stream()
                .map(ref -> ref.getFieldName() != null ? ref.getFieldName() : "[" + ref.getIndex() + "]")
                .collect(Collectors.joining("."));
    }

    private static String truncate(String s, int max) {
        if (s == null) return null;
        return s.length() <= max ? s : s.substring(0, max) + "...";
    }
}
