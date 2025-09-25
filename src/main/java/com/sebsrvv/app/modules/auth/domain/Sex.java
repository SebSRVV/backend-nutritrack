package com.sebsrvv.app.modules.auth.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum Sex {
    MALE("male"),
    FEMALE("female");

    private final String dbValue;

    Sex(String dbValue) { this.dbValue = dbValue; }

    /** Serializa siempre lo que espera la DB (minúsculas) */
    @JsonValue
    public String dbValue() { return dbValue; }

    /** Acepta "male", "MALE", "Male", etc. */
    @JsonCreator
    public static Sex from(String value) {
        if (value == null) return null;
        String v = value.trim().toLowerCase();
        return switch (v) {
            case "male"   -> MALE;
            case "female" -> FEMALE;
            default -> throw new IllegalArgumentException("sex inválido: " + value);
        };
    }
}
