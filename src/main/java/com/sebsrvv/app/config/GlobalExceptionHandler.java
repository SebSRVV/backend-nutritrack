package com.sebsrvv.app.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

import java.sql.SQLException;
import java.util.*;

/** Manejador global de errores comunes */
@RestControllerAdvice
public class GlobalExceptionHandler {

    /** 400 - Validación */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> v = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String f = (err instanceof FieldError fe) ? fe.getField() : err.getObjectName();
            Object r = (err instanceof FieldError fe) ? fe.getRejectedValue() : null;
            v.add(new ApiError.FieldViolation(f, err.getDefaultMessage(), r));
        });
        return build(HttpStatus.BAD_REQUEST, "validation_error", "Los datos enviados no son válidos", req, v);
    }

    /** 400 - Violación de constraints */
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolation(ConstraintViolationException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> v = new ArrayList<>();
        for (ConstraintViolation<?> c : ex.getConstraintViolations()) {
            v.add(new ApiError.FieldViolation(c.getPropertyPath().toString(), c.getMessage(), c.getInvalidValue()));
        }
        return build(HttpStatus.BAD_REQUEST, "validation_error", "Los datos enviados no son válidos", req, v);
    }

    /** 400 - BindException */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiError> handleBind(BindException ex, HttpServletRequest req) {
        List<ApiError.FieldViolation> v = new ArrayList<>();
        ex.getBindingResult().getAllErrors().forEach(err -> {
            String f = (err instanceof FieldError fe) ? fe.getField() : err.getObjectName();
            Object r = (err instanceof FieldError fe) ? fe.getRejectedValue() : null;
            v.add(new ApiError.FieldViolation(f, err.getDefaultMessage(), r));
        });
        return build(HttpStatus.BAD_REQUEST, "validation_error", "Los datos enviados no son válidos", req, v);
    }

    /** 400 - JSON malformado */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "malformed_json", "El cuerpo de la solicitud no es válido", req, null);
    }

    /** 400 - Falta parámetro */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiError> handleMissing(MissingServletRequestParameterException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "missing_parameter",
                "Falta un parámetro requerido: " + ex.getParameterName(), req, null);
    }

    /** 400 - Tipo incorrecto */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiError> handleType(MethodArgumentTypeMismatchException ex, HttpServletRequest req) {
        return build(HttpStatus.BAD_REQUEST, "type_mismatch", "El tipo del parámetro no es válido", req, null);
    }

    /** 405 - Método no permitido */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ApiError> handleMethod(HttpRequestMethodNotSupportedException ex, HttpServletRequest req) {
        return build(HttpStatus.METHOD_NOT_ALLOWED, "method_not_allowed",
                "El método HTTP no está permitido", req, null);
    }

    /** 401 - Credenciales inválidas */
    @ExceptionHandler({ BadCredentialsException.class, AuthenticationException.class })
    public ResponseEntity<ApiError> handleAuth(Exception ex, HttpServletRequest req) {
        return build(HttpStatus.UNAUTHORIZED, "invalid_credentials", "Credenciales inválidas o no autenticado", req, null);
    }

    /** 403 - Acceso denegado */
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiError> handleAccessDenied(AccessDeniedException ex, HttpServletRequest req) {
        return build(HttpStatus.FORBIDDEN, "access_denied", "No tienes permisos para este recurso", req, null);
    }

    /** 422 - Violaciones de integridad */
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleIntegrity(DataIntegrityViolationException ex, HttpServletRequest req) {
        SQLException sql = findSqlException(ex);
        String state = sql != null ? sql.getSQLState() : null;
        String constraint = sql != null ? extractConstraint(sql.getMessage()) : null;
        if ("23505".equals(state)) {
            String code = mapConstraintToCode(constraint);
            String msg = translateMessage(code);
            return build(HttpStatus.UNPROCESSABLE_ENTITY, code, msg, req, null);
        }
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "data_integrity_violation",
                "Violación de integridad de datos", req, null);
    }

    /** 500 - Error DB genérico */
    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ApiError> handleDB(DataAccessException ex, HttpServletRequest req) {
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "database_error",
                "Error al acceder a la base de datos", req, null);
    }

    /** Errores de APIs externas */
    @ExceptionHandler(HttpStatusCodeException.class)
    public ResponseEntity<ApiError> handleRest(HttpStatusCodeException ex, HttpServletRequest req) {
        return buildFromRemoteResponse(req, ex.getStatusCode().value(), ex.getResponseBodyAsString());
    }

    @ExceptionHandler(WebClientResponseException.class)
    public ResponseEntity<ApiError> handleWeb(WebClientResponseException ex, HttpServletRequest req) {
        return buildFromRemoteResponse(req, ex.getRawStatusCode(), ex.getResponseBodyAsString());
    }

    /** ResponseStatusException */
    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ApiError> handleResponse(ResponseStatusException ex, HttpServletRequest req) {
        HttpStatus s = HttpStatus.valueOf(ex.getStatusCode().value());
        String msg = ex.getReason() != null ? ex.getReason() : s.getReasonPhrase();
        return build(s, "error", msg, req, null);
    }

    /** 500 - Error genérico */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneric(Exception ex, HttpServletRequest req) {
        Throwable c = ex;
        while (c != null && c.getCause() != c) {
            if (c instanceof HttpStatusCodeException h)
                return buildFromRemoteResponse(req, h.getStatusCode().value(), h.getResponseBodyAsString());
            if (c instanceof WebClientResponseException w)
                return buildFromRemoteResponse(req, w.getRawStatusCode(), w.getResponseBodyAsString());
            if (c instanceof SQLException s && "23505".equals(s.getSQLState())) {
                String constraint = extractConstraint(s.getMessage());
                String code = mapConstraintToCode(constraint);
                String msg = translateMessage(code);
                return build(HttpStatus.UNPROCESSABLE_ENTITY, code, msg, req, null);
            }
            c = c.getCause();
        }
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "internal_error",
                "Ha ocurrido un error interno", req, null);
    }

    /** Construye respuesta estándar */
    private ResponseEntity<ApiError> build(HttpStatus st, String code, String msg,
                                           HttpServletRequest req, List<ApiError.FieldViolation> v) {
        ApiError body = new ApiError(st.value(), st.getReasonPhrase(), code, msg,
                req != null ? req.getRequestURI() : null, null, v);
        return ResponseEntity.status(st).body(body);
    }

    /** Decodifica error HTTP remoto */
    private ResponseEntity<ApiError> buildFromRemoteResponse(HttpServletRequest req, int raw, String body) {
        HttpStatus s = HttpStatus.resolve(raw) != null ? HttpStatus.valueOf(raw) : HttpStatus.BAD_GATEWAY;
        String code = s.is4xxClientError() ? "client_error" : "upstream_error";
        String msg = s.getReasonPhrase();
        if (body != null && !body.isBlank()) {
            try {
                JsonNode root = new ObjectMapper().readTree(body);
                if (root.hasNonNull("error_code")) code = root.get("error_code").asText();
                else if (root.hasNonNull("code")) code = root.get("code").asText();
                if (root.hasNonNull("msg")) msg = root.get("msg").asText();
                else if (root.hasNonNull("message")) msg = root.get("message").asText();
                msg = translateMessage(code);
            } catch (Exception ignore) {}
        }
        return build(s, code, msg, req, null);
    }

    /** Utilidades SQL */
    private static SQLException findSqlException(Throwable ex) {
        while (ex != null && ex.getCause() != ex) {
            if (ex instanceof SQLException sql) return sql;
            ex = ex.getCause();
        }
        return null;
    }

    private static String extractConstraint(String msg) {
        if (msg == null) return null;
        int i = msg.indexOf("unique constraint");
        if (i < 0) return null;
        int q1 = msg.indexOf('"', i);
        int q2 = q1 >= 0 ? msg.indexOf('"', q1 + 1) : -1;
        return (q1 >= 0 && q2 > q1) ? msg.substring(q1 + 1, q2) : null;
    }

    /** Mapas de constraint → código */
    private static final Map<String, String> UNIQUE_CONSTRAINT_MAP = new HashMap<>();
    static {
        UNIQUE_CONSTRAINT_MAP.put("profiles_username_key", "username_already_exists");
        UNIQUE_CONSTRAINT_MAP.put("users_email_key", "email_already_exists");
    }

    private static String mapConstraintToCode(String constraint) {
        if (constraint == null) return "data_integrity_violation";
        return UNIQUE_CONSTRAINT_MAP.getOrDefault(constraint.trim().toLowerCase(), "data_integrity_violation");
    }

    /** Traducciones simples */
    private static String translateMessage(String code) {
        return switch (code) {
            case "user_already_exists" -> "El usuario ya se encuentra registrado";
            case "email_already_exists" -> "El correo ya se encuentra registrado";
            case "username_already_exists" -> "El nombre de usuario ya se encuentra registrado";
            case "invalid_credentials" -> "Credenciales inválidas o no autenticado";
            case "user_not_found" -> "No se encontró el usuario";
            case "invalid_password" -> "Contraseña inválida";
            case "invalid_email" -> "Formato de correo inválido";
            default -> "Error en la solicitud o procesamiento";
        };
    }
}
