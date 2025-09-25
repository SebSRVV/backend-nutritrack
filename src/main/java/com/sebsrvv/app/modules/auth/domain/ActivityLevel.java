package com.sebsrvv.app.modules.auth.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

public enum ActivityLevel {
    SEDENTARY("sedentary"),
    MODERATE("moderate"),
    VERY_ACTIVE("very_active");

    private final String dbValue;

    ActivityLevel(String dbValue) { this.dbValue = dbValue; }

    /** Siempre serializa el valor que espera la DB (snake_case) */
    @JsonValue
    public String dbValue() { return dbValue; }

    /** Acepta veryActive / very-active / VERY_ACTIVE / very_active */
    @JsonCreator
    public static ActivityLevel from(Object raw) {
        if (raw == null) return null;
        String s = String.valueOf(raw).trim();
        if (s.isEmpty()) return null;

        String key = s.toLowerCase(Locale.ROOT).replace('-', '_');
        return switch (key) {
            case "sedentary"   -> SEDENTARY;
            case "moderate"    -> MODERATE;
            case "very_active" -> VERY_ACTIVE;
            default -> throw new IllegalArgumentException("activity_level inválido: " + s);
        };
    }
}
