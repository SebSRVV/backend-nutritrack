package com.sebsrvv.app.modules.users.application;

import com.sebsrvv.app.modules.users.domain.UserReport;

public class GeneratedReport {
    private final UserReport report;
    private final byte[] pdf;

    public GeneratedReport(UserReport report, byte[] pdf) {
        this.report = report;
        this.pdf = pdf;
    }

    public UserReport getReport() { return report; }
    public byte[] getPdf() { return pdf; }
}
