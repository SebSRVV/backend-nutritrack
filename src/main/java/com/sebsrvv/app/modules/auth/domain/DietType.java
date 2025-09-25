package com.sebsrvv.app.modules.auth.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import java.util.Locale;

public enum DietType {
    caloric_deficit("caloric_deficit"),
    surplus("surplus"),
    low_carb("low_carb"),
    maintenance("maintenance");

    private final String dbValue;
    DietType(String dbValue) { this.dbValue = dbValue; }

    @JsonValue public String dbValue() { return dbValue; }

    @JsonCreator
    public static DietType from(Object raw) {
        if (raw == null) return null;
        String s = String.valueOf(raw).trim();
        if (s.isEmpty()) return null;
        String key = s.toLowerCase(Locale.ROOT).replace('-', '_');
        return switch (key) {
            case "caloric_deficit" -> caloric_deficit;
            case "surplus"         -> surplus;
            case "low_carb"        -> low_carb;
            case "maintenance"     -> maintenance;
            default -> throw new IllegalArgumentException("diet_type inválido: " + s);
        };
    }
}
