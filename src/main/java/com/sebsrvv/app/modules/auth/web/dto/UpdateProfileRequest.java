package com.sebsrvv.app.modules.auth.web.dto;

import com.sebsrvv.app.modules.auth.domain.Sex;
import com.sebsrvv.app.modules.auth.domain.ActivityLevel;
import com.sebsrvv.app.modules.auth.domain.DietType;

public record UpdateProfileRequest(
        Sex sex,
        Integer height_cm,
        Integer weight_kg,
        ActivityLevel activity_level,
        DietType diet_type
) {}
