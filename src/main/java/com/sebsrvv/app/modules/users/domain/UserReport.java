package com.sebsrvv.app.modules.users.domain;

import java.time.Instant;
import java.util.UUID;

public class UserReport {
    private UUID id;
    private String name;
    private ReportType type;
    private String url;
    private Instant generatedAt;

    public UserReport(UUID id, String name, ReportType type, String url, Instant generatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.url = url;
        this.generatedAt = generatedAt;
    }

    public UUID getId() { return id; }
    public String getName() { return name; }
    public ReportType getType() { return type; }
    public String getUrl() { return url; }
    public Instant getGeneratedAt() { return generatedAt; }
}
