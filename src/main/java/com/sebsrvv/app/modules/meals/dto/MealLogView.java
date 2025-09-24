package com.sebsrvv.app.modules.meals.dto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record MealLogView(
        UUID id,
        UUID user_id,
        String description,
        Integer calories,
        Double protein_g,
        Double carbs_g,
        Double fat_g,
        String meal_type,
        OffsetDateTime logged_at,
        String meal_categories
) {}
