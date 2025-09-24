package com.sebsrvv.app.modules.meals.dto;

import jakarta.validation.constraints.*;
import java.time.OffsetDateTime;
import java.util.List;

public record CreateMealLogDto(
        @NotBlank String description,
        Integer calories,
        @PositiveOrZero Double protein_g,
        @PositiveOrZero Double carbs_g,
        @PositiveOrZero Double fat_g,
        @NotBlank String meal_type,
        OffsetDateTime logged_at,
        String meal_categories,
        String ai_items,
        List<Integer> category_ids
) {}
