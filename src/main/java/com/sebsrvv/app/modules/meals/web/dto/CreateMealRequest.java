package com.sebsrvv.app.modules.meals.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;
import java.util.UUID;

public record CreateMealRequest(
        UUID userId,
        String description,
        int calories,
        BigDecimal proteinGrams,
        BigDecimal carbsGrams,
        BigDecimal fatGrams,
        String mealType,
        OffsetDateTime loggedAt,
        Set<Integer> categoryIds
) {}
