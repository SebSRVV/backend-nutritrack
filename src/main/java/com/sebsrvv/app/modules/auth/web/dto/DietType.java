package com.sebsrvv.app.modules.auth.web.dto;

public enum DietType {
    LOW_CARB("low_carb"),
    CALORIC_DEFICIT("caloric_deficit"),
    SURPLUS("surplus");

    private final String dbValue;
    DietType(String dbValue) { this.dbValue = dbValue; }
    public String dbValue() { return dbValue; }
}
