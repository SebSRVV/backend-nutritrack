package com.sebsrvv.app.modules.auth.web.dto;

import java.time.LocalDate;

public record UpdateProfileRequest(
        String username,
        Sex sex,
        Short height_cm,
        java.math.BigDecimal weight_kg,
        LocalDate dob,
        ActivityLevel activity_level,
        DietType diet_type
) {}
