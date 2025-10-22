package com.sebsrvv.app.modules.auth.web.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

public record RegisterRequest(
        String email,
        String password,
        String username,
        Sex sex,
        Short height_cm,
        BigDecimal weight_kg,
        LocalDate dob,
        ActivityLevel activity_level,
        DietType diet_type
) {}
