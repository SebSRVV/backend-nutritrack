package com.sebsrvv.app.modules.practice.exception;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.LocalDateTime;

public class SuccessResponse {
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime timestamp;
    private String code;
    private String message;

    public SuccessResponse() {
        this.timestamp = LocalDateTime.now();
    }

    public SuccessResponse(String code, String message) {
        this.timestamp = LocalDateTime.now();
        this.code = code;
        this.message = message;
    }

    // Getters y Setters
    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}