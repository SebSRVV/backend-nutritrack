package com.sebsrvv.app.modules.meals.web.dto;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.Set;

public record UpdateMealRequest(
        String description,
        Integer calories,
        BigDecimal proteinGrams,
        BigDecimal carbsGrams,
        BigDecimal fatGrams,
        String mealType,
        OffsetDateTime loggedAt,
        Set<Integer> categoryIds
) {}
