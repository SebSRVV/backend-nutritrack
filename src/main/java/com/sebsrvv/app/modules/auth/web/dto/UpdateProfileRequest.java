package com.sebsrvv.app.modules.auth.web.dto;

import com.sebsrvv.app.modules.auth.domain.ActivityLevel;
import com.sebsrvv.app.modules.auth.domain.DietType;
import com.sebsrvv.app.modules.auth.domain.Sex;

public record UpdateProfileRequest(
        Sex sex,                       // usa el enum del dominio (MALE/FEMALE)
        Integer height_cm,
        Integer weight_kg,
        ActivityLevel activity_level,  // sedentary | moderate | very_active
        DietType diet_type             // caloric_deficit | surplus | low_carb | maintenance
) {}
