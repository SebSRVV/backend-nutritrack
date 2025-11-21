package com.sebsrvv.app.modules.auth.web.dto;

public record UpdateProfileResponse(
        String id,
        String username,
        String sex,
        Short height_cm,
        java.math.BigDecimal weight_kg,
        String activity_level,
        String diet_type,
        java.math.BigDecimal bmi,
        Integer age,
        Integer days_to_birthday,
        Integer recommended_kcal,
        String updated_at
) {}
