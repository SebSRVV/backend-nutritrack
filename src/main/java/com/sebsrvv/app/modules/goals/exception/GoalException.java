package com.sebsrvv.app.modules.goals.exception;

import org.springframework.http.HttpStatus;

public abstract class GoalException extends RuntimeException {
    private final HttpStatus status;
    private final String code;
    private final String detail;

    protected GoalException(HttpStatus status, String code, String detail) {
        super(detail);
        this.status = status;
        this.code = code;
        this.detail = detail;
    }

    public HttpStatus getStatus() { return status; }
    public String getCode() { return code; }
    public String getDetail() { return detail; }
}
