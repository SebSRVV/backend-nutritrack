package com.sebsrvv.app.modules.auth.web.dto;

import java.util.Map;

public record MeResponse(
        String id,
        String email,
        String role,
        Map<String, Object> app_metadata,
        Map<String, Object> user_metadata,
        String created_at,
        String updated_at
) {}
