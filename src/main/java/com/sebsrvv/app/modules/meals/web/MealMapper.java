package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.domain.MealLog;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MealMapper {

    public MealLog toEntity(String userId, MealRequest request) {
        MealLog meal = new MealLog();
        meal.setUserId(userId);
        meal.setMealType(request.getMealType());
        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProtein_g(request.getProtein_g() == null ? 0.0 : request.getProtein_g());
        meal.setCarbs_g(request.getCarbs_g() == null ? 0.0 : request.getCarbs_g());
        meal.setFat_g(request.getFat_g() == null ? 0.0 : request.getFat_g());
        meal.setLoggedAt(request.getLoggedAt());
        meal.setMealItems(new ArrayList<>());
        return meal;
    }

    public void updateEntityFromRequest(MealLog meal, MealRequest request) {
        if (request.getMealType() != null) meal.setMealType(request.getMealType());
        if (request.getDescription() != null) meal.setDescription(request.getDescription());
        if (request.getCalories() != null) meal.setCalories(request.getCalories());
        if (request.getProtein_g() != null) meal.setProtein_g(request.getProtein_g());
        if (request.getCarbs_g() != null) meal.setCarbs_g(request.getCarbs_g());
        if (request.getFat_g() != null) meal.setFat_g(request.getFat_g());
        if (request.getLoggedAt() != null) meal.setLoggedAt(request.getLoggedAt());
    }

    public MealResponse toResponse(MealLog meal) {
        MealResponse dto = new MealResponse();
        dto.setId(meal.getId());
        dto.setMealType(meal.getMealType());
        dto.setDescription(meal.getDescription());
        dto.setCalories(meal.getCalories());
        dto.setProtein_g(meal.getProtein_g());
        dto.setCarbs_g(meal.getCarbs_g());
        dto.setFat_g(meal.getFat_g());
        dto.setLoggedAt(meal.getLoggedAt());
        dto.setMealItems(meal.getMealItems());
        return dto;
    }
}
