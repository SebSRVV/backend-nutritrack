package com.sebsrvv.app.modules.goals.web.dto;

public enum ErrorCode {

    // ===== GOALS (3) =====
    GOALS_EMPTY_BODY("GOALS_001", "Body vacío: se requiere información de la meta."),
    GOALS_INVALID_WEEKLY_TARGET("GOALS_002", "weekly_target debe estar entre 1 y 7."),
    GOALS_INVALID_DATE_RANGE("GOALS_003", "end_date no puede ser anterior a start_date."),

    // ===== GOAL PROGRESS (3) =====
    PROGRESS_DUPLICATE_LOGDATE("PROGRESS_001", "Ya existe un registro para esa fecha (log_date)."),
    PROGRESS_NOT_FOUND("PROGRESS_002", "No se encontró el progreso solicitado."),
    PROGRESS_INVALID_VALUE("PROGRESS_003", "Valor inválido para el tipo de meta.");

    private final String code;
    private final String defaultMessage;

    ErrorCode(String code, String defaultMessage) {
        this.code = code;
        this.defaultMessage = defaultMessage;
    }

    public String code() { return code; }
    public String defaultMessage() { return defaultMessage; }
}
