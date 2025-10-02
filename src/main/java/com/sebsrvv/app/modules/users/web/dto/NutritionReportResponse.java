package com.sebsrvv.app.modules.users.web.dto;

public class NutritionReportResponse {
    private String id;
    private String reportName;
    private String reportType; // "nutrition_pdf"
    private String reportUrl;
    private String generatedAt; // ISO-8601

    public NutritionReportResponse() {}
    public NutritionReportResponse(String id, String reportName, String reportType,
                                   String reportUrl, String generatedAt) {
        this.id = id;
        this.reportName = reportName;
        this.reportType = reportType;
        this.reportUrl = reportUrl;
        this.generatedAt = generatedAt;
    }

    public String getId() { return id; }
    public String getReportName() { return reportName; }
    public String getReportType() { return reportType; }
    public String getReportUrl() { return reportUrl; }
    public String getGeneratedAt() { return generatedAt; }
    public void setId(String id) { this.id = id; }
    public void setReportName(String reportName) { this.reportName = reportName; }
    public void setReportType(String reportType) { this.reportType = reportType; }
    public void setReportUrl(String reportUrl) { this.reportUrl = reportUrl; }
    public void setGeneratedAt(String generatedAt) { this.generatedAt = generatedAt; }
}
