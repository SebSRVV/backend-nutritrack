package com.sebsrvv.app.modules.auth.web.dto;

public enum ActivityLevel {
    SEDENTARY("sedentary"),
    MODERATE("moderate"),
    VERY_ACTIVE("very_active");

    private final String dbValue;
    ActivityLevel(String dbValue) { this.dbValue = dbValue; }
    public String dbValue() { return dbValue; }
}
