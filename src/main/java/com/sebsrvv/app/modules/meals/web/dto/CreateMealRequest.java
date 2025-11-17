package com.sebsrvv.app.modules.meals.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public record CreateMealRequest(
        String description,
        Integer calories,
        BigDecimal proteinGrams,
        BigDecimal carbsGrams,
        BigDecimal fatGrams,
        String mealType,                // "breakfast", "lunch", etc. (case-insensitive)
        OffsetDateTime loggedAt,
        Set<Integer> categoryIds
) {}
