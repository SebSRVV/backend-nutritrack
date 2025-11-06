package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealCategory;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;

public class MealMapper {

    public static Meal toEntity(MealRequest request, MealCategory category) {
        Meal meal = new Meal();
        meal.setUserId(request.getUserId());
        meal.setName(request.getName());
        meal.setMealType(request.getMealType());
        meal.setCalories(request.getCalories());
        meal.setNote(request.getNote());
        meal.setLoggedAt(request.getLoggedAt());
        meal.setCategory(category);
        return meal;
    }

    public static MealResponse toResponse(Meal meal) {
        MealResponse response = new MealResponse();
        response.setId(meal.getId());
        response.setUserId(meal.getUserId());
        response.setName(meal.getName());
        response.setMealType(meal.getMealType());
        response.setCalories(meal.getCalories());
        response.setNote(meal.getNote());
        response.setLoggedAt(meal.getLoggedAt());

        if (meal.getCategory() != null) {
            response.setCategoryId(meal.getCategory().getId());
            response.setCategoryName(meal.getCategory().getName());
        }

        return response;
    }
}
