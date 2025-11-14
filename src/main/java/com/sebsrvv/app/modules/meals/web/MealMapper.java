// src/main/java/com/sebsrvv/app/modules/meals/web/MealMapper.java
package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Mapper simple: request -> entity (necesita userId) y entity -> response.
 * No establece category; si usas category añade overloads que reciban categoryId.
 */
@Component
public class MealMapper {

    public Meal toEntity(UUID userId, MealRequest request) {
        if (request == null) return null;
        Meal meal = new Meal();
        meal.setUserId(userId);
        meal.setMealType(request.getMealType());
        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProtein_g(request.getProtein_g());
        meal.setCarbs_g(request.getCarbs_g());
        meal.setFat_g(request.getFat_g());
        meal.setLoggedAt(request.getLoggedAt());
        return meal;
    }

    public void updateEntityFromRequest(Meal meal, MealRequest request) {
        if (meal == null || request == null) return;
        meal.setMealType(request.getMealType());
        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProtein_g(request.getProtein_g());
        meal.setCarbs_g(request.getCarbs_g());
        meal.setFat_g(request.getFat_g());
        meal.setLoggedAt(request.getLoggedAt());
    }

    public MealResponse toResponse(Meal meal) {
        if (meal == null) return null;
        MealResponse response = new MealResponse();
        response.setId(meal.getId());
        response.setMealType(meal.getMealType());
        response.setDescription(meal.getDescription());
        response.setCalories(meal.getCalories());
        response.setProtein_g(meal.getProtein_g());
        response.setCarbs_g(meal.getCarbs_g());
        response.setFat_g(meal.getFat_g());
        response.setLoggedAt(meal.getLoggedAt());
        return response;
    }
}
