package com.sebsrvv.app.modules.goals.web;

import java.time.OffsetDateTime;

public class ErrorResponse {
    private String code;       // e.g., GOALS_001
    private String message;    // mensaje legible
    private String path;       // endpoint
    private OffsetDateTime timestamp = OffsetDateTime.now();

    public ErrorResponse() {}

    public ErrorResponse(String code, String message, String path) {
        this.code = code;
        this.message = message;
        this.path = path;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getPath() { return path; }
    public void setPath(String path) { this.path = path; }

    public OffsetDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(OffsetDateTime timestamp) { this.timestamp = timestamp; }
}
