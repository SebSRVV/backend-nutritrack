package com.sebsrvv.app.modules.auth.web.dto;

import java.util.Map;

public record LoginResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        Integer expiresIn,
        Map<String, Object> user
) {}
