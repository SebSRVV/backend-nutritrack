package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.domain.MealLog;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Component
public class MealMapper {

    // ---- CREATE ----
    public MealLog toEntity(String userId, MealRequest request) {
        MealLog meal = new MealLog();

        meal.setUserId(userId);

        // PASAR ENUM DIRECTO → entidad genera string en minúsculas
        if (request.getMealType() != null) {
            meal.setMealType(request.getMealType());
        } else {
            meal.setMealType((MealType) null);
        }

        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProtein_g(request.getProtein_g());
        meal.setCarbs_g(request.getCarbs_g());
        meal.setFat_g(request.getFat_g());

        // LocalDate → Instant
        if (request.getLoggedAt() != null) {
            meal.setLoggedAt(
                    request.getLoggedAt()
                            .atStartOfDay(ZoneOffset.UTC)
                            .toInstant()
            );
        }

        return meal;
    }

    // ---- UPDATE ----
    public void updateEntityFromRequest(MealLog meal, MealRequest request) {

        if (request.getMealType() != null) {
            meal.setMealType(request.getMealType());
        }

        if (request.getDescription() != null) meal.setDescription(request.getDescription());
        if (request.getCalories() != null) meal.setCalories(request.getCalories());
        if (request.getProtein_g() != null) meal.setProtein_g(request.getProtein_g());
        if (request.getCarbs_g() != null) meal.setCarbs_g(request.getCarbs_g());
        if (request.getFat_g() != null) meal.setFat_g(request.getFat_g());

        if (request.getLoggedAt() != null) {
            meal.setLoggedAt(
                    request.getLoggedAt()
                            .atStartOfDay(ZoneOffset.UTC)
                            .toInstant()
            );
        }
    }

    // ---- RESPONSE ----
    public MealResponse toResponse(MealLog meal) {
        MealResponse dto = new MealResponse();

        dto.setId(meal.getId());

        // STRING → ENUM (seguro)
        try {
            dto.setMealType(
                    meal.getMealType() == null
                            ? null
                            : MealType.valueOf(meal.getMealType().toUpperCase())
            );
        } catch (Exception e) {
            dto.setMealType(null);
        }

        dto.setDescription(meal.getDescription());
        dto.setCalories(meal.getCalories());
        dto.setProtein_g(meal.getProtein_g());
        dto.setCarbs_g(meal.getCarbs_g());
        dto.setFat_g(meal.getFat_g());

        // Instant → LocalDate
        if (meal.getLoggedAt() != null) {
            dto.setLoggedAt(
                    meal.getLoggedAt()
                            .atZone(ZoneOffset.UTC)
                            .toLocalDate()
            );
        }

        dto.setMealItems(meal.getMealItems());

        return dto;
    }
}
