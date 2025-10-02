package com.sebsrvv.app.modules.users.domain;

public enum ReportType {
    NUTRITION_PDF("nutrition_pdf");
    private final String wire;
    ReportType(String wire){ this.wire = wire; }
    public String wire() { return wire; }
}
