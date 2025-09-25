package com.sebsrvv.app.modules.auth.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

public enum DietType {
    CALORIC_DEFICIT("caloric_deficit"),
    SURPLUS("surplus"),
    LOW_CARB("low_carb"),
    MAINTENANCE("maintenance");

    private final String dbValue;

    DietType(String dbValue) { this.dbValue = dbValue; }

    @JsonValue
    public String dbValue() { return dbValue; }

    @JsonCreator
    public static DietType from(Object raw) {
        if (raw == null) return null;
        String s = String.valueOf(raw).trim();
        if (s.isEmpty()) return null;

        String key = s.toLowerCase(Locale.ROOT).replace('-', '_');
        return switch (key) {
            case "caloric_deficit" -> CALORIC_DEFICIT;
            case "surplus"         -> SURPLUS;
            case "low_carb"        -> LOW_CARB;
            case "maintenance"     -> MAINTENANCE;
            default -> throw new IllegalArgumentException("diet_type inválido: " + s);
        };
    }
}
