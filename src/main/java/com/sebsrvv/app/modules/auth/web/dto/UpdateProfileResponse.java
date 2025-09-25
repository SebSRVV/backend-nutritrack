package com.sebsrvv.app.modules.auth.web.dto;

public record UpdateProfileResponse(
        String id,
        String sex,
        Integer height_cm,
        Integer weight_kg,
        String activity_level,
        String diet_type,
        Double bmi,
        String updated_at
) {}
