package com.sebsrvv.app.modules.auth.web.dto;

public record LoginRequest(
        String email,
        String password
) {}
