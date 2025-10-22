package com.sebsrvv.app.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.List;

/**
 * Modelo estandar para respuestas de error.
 * Se utiliza en toda la aplicacion para mantener consistencia
 * entre los distintos controladores y modulos.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiError {

    // Marca de tiempo ISO-8601 en UTC
    private final OffsetDateTime timestamp = OffsetDateTime.now(ZoneOffset.UTC);

    // Codigo numerico HTTP
    private final int status;

    // Frase asociada al codigo HTTP (ej: "Bad Request", "Internal Server Error")
    private final String error;

    // Codigo de negocio o tecnico (estable, legible por clientes)
    private final String code;

    // Mensaje legible para humanos
    private final String message;

    // Ruta solicitada
    private final String path;

    // ID de la peticion (si usas MDC o trazabilidad)
    private final String requestId;

    // Lista opcional de errores de validacion
    private final List<FieldViolation> violations;

    public ApiError(int status,
                    String error,
                    String code,
                    String message,
                    String path,
                    String requestId,
                    List<FieldViolation> violations) {
        this.status = status;
        this.error = error;
        this.code = code;
        this.message = message;
        this.path = path;
        this.requestId = requestId;
        this.violations = violations;
    }

    // ===== Getters =====

    public OffsetDateTime getTimestamp() {
        return timestamp;
    }

    public int getStatus() {
        return status;
    }

    public String getError() {
        return error;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getPath() {
        return path;
    }

    public String getRequestId() {
        return requestId;
    }

    public List<FieldViolation> getViolations() {
        return violations;
    }

    // ===== Clase interna para errores de validacion =====

    /**
     * Representa un error de validacion de un campo especifico.
     */
    public static class FieldViolation {
        private final String field;
        private final String message;
        private final Object rejectedValue;

        public FieldViolation(String field, String message, Object rejectedValue) {
            this.field = field;
            this.message = message;
            this.rejectedValue = rejectedValue;
        }

        public String getField() {
            return field;
        }

        public String getMessage() {
            return message;
        }

        public Object getRejectedValue() {
            return rejectedValue;
        }
    }
}
