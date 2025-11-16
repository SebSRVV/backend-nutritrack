package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.domain.MealLog;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.Collections;

@Component
public class MealMapper {

    // ---- CREATE ----
    public MealLog toEntity(String userId, MealRequest request) {
        if (request == null) return null;
        MealLog meal = new MealLog();

        meal.setUserId(userId);
        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProtein_g(request.getProtein_g());
        meal.setCarbs_g(request.getCarbs_g());
        meal.setFat_g(request.getFat_g());
        meal.setMealItems(request.getMealItems() == null ? Collections.emptyList() : request.getMealItems());

        if (request.getMealType() != null) {
            meal.setMealType(request.getMealType().name());
        }

        if (request.getLoggedAt() != null) {
            Instant inst = request.getLoggedAt().atStartOfDay().toInstant(ZoneOffset.UTC);
            meal.setLoggedAt(inst);
        }
        return meal;
    }

    // ---- UPDATE (in-place) ----
    public void updateEntityFromRequest(MealLog meal, MealRequest request) {
        if (meal == null || request == null) return;

        if (request.getMealType() != null) meal.setMealType(request.getMealType().name());
        if (request.getDescription() != null) meal.setDescription(request.getDescription());
        if (request.getCalories() != null) meal.setCalories(request.getCalories());
        if (request.getProtein_g() != null) meal.setProtein_g(request.getProtein_g());
        if (request.getCarbs_g() != null) meal.setCarbs_g(request.getCarbs_g());
        if (request.getFat_g() != null) meal.setFat_g(request.getFat_g());
        if (request.getMealItems() != null) meal.setMealItems(request.getMealItems());
        if (request.getLoggedAt() != null) meal.setLoggedAt(request.getLoggedAt().atStartOfDay().toInstant(ZoneOffset.UTC));
    }

    // ---- TO DTO ----
    public MealResponse toResponse(MealLog meal) {
        if (meal == null) return null;
        MealResponse dto = new MealResponse();
        dto.setId(meal.getId());
        dto.setDescription(meal.getDescription());
        dto.setCalories(meal.getCalories());
        dto.setProtein_g(meal.getProtein_g());
        dto.setCarbs_g(meal.getCarbs_g());
        dto.setFat_g(meal.getFat_g());
        dto.setMealItems(meal.getMealItems() == null ? Collections.emptyList() : meal.getMealItems());

        if (meal.getMealType() != null) {
            try {
                dto.setMealType(MealType.valueOf(meal.getMealType()));
            } catch (Exception ex) {
                dto.setMealType(null);
            }
        }

        if (meal.getLoggedAt() != null) {
            dto.setLoggedAt(LocalDate.ofInstant(meal.getLoggedAt(), ZoneOffset.UTC));
        }

        return dto;
    }
}
