package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.domain.MealLog;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

@Component
public class MealMapper {

    public MealLog toEntity(Long userId, MealRequest request) {
        MealLog meal = new MealLog();
        meal.setUserId(userId);
        meal.setMealType(request.getMealType());
        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProtein_g(request.getProtein_g());
        meal.setCarbs_g(request.getCarbs_g());
        meal.setFat_g(request.getFat_g());
        meal.setLoggedAt(request.getLoggedAt());
        meal.setMealItems(new ArrayList<>()); // inicializamos vacío
        return meal;
    }

    public void updateEntityFromRequest(MealLog meal, MealRequest request) {
        meal.setMealType(request.getMealType());
        meal.setDescription(request.getDescription());
        meal.setCalories(request.getCalories());
        meal.setProtein_g(request.getProtein_g());
        meal.setCarbs_g(request.getCarbs_g());
        meal.setFat_g(request.getFat_g());
        meal.setLoggedAt(request.getLoggedAt());
    }

    public MealResponse toResponse(MealLog meal) {
        MealResponse response = new MealResponse();
        response.setId(meal.getId());
        response.setMealType(meal.getMealType());
        response.setDescription(meal.getDescription());
        response.setCalories(meal.getCalories());
        response.setProtein_g(meal.getProtein_g());
        response.setCarbs_g(meal.getCarbs_g());
        response.setFat_g(meal.getFat_g());
        response.setLoggedAt(meal.getLoggedAt());
        response.setMealItems(meal.getMealItems());
        return response;
    }
}
