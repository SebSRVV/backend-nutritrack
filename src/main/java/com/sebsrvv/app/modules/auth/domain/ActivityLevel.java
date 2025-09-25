package com.sebsrvv.app.modules.auth.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

public enum ActivityLevel {
    sedentary("sedentary"),
    moderate("moderate"),
    very_active("very_active");

    private final String dbValue;
    ActivityLevel(String dbValue) { this.dbValue = dbValue; }

    @JsonValue public String dbValue() { return dbValue; }

    @JsonCreator
    public static ActivityLevel from(Object raw) {
        if (raw == null) return null;
        String s = String.valueOf(raw).trim();
        if (s.isEmpty()) return null;
        String key = s.toLowerCase(Locale.ROOT).replace('-', '_');
        return switch (key) {
            case "sedentary"   -> sedentary;
            case "moderate"    -> moderate;
            case "very_active" -> very_active;
            default -> throw new IllegalArgumentException("activity_level inválido: " + s);
        };
    }
}
