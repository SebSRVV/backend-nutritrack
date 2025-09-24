package com.sebsrvv.app.modules.profile.dto;

import java.time.*;
import java.util.UUID;

public record ProfileView(
        UUID id, String username, LocalDate dob, String sex,
        Integer height_cm, Double weight_kg, Double bmi,
        String activity_level, String diet_type,
        OffsetDateTime created_at, OffsetDateTime updated_at
) {}
