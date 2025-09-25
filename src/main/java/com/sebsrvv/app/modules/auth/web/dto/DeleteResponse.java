package com.sebsrvv.app.modules.auth.web.dto;

public record DeleteResponse(
        String status,   // "deleted" | "rejected" | "forbidden"
        String message
) {}
