package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.FoodCategory;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;

public class MealMapper {

    // Convertir DTO -> Entidad
    public static Meal toEntity(MealRequest request) {
        Meal meal = new Meal();
        meal.setUserId(request.getUserId());
        meal.setName(request.getName());
        meal.setMealType(request.getMealType());
        meal.setCalories(request.getCalories());
        meal.setNote(request.getNote());
        meal.setLoggedAt(request.getLoggedAt());

        // Si existe categoryId, asignar objeto FoodCategory con ese UUID
        if (request.getCategoryId() != null) {
            FoodCategory category = new FoodCategory();
            category.setId(request.getCategoryId());
            meal.setCategory(category);
        }

        return meal;
    }

    // Convertir Entidad -> DTO
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
        }

        return response;
    }

    // Actualizar entidad existente desde DTO
    public static void updateEntityFromDto(MealRequest request, Meal meal) {
        if (request.getName() != null) meal.setName(request.getName());
        if (request.getMealType() != null) meal.setMealType(request.getMealType());
        if (request.getCalories() != null) meal.setCalories(request.getCalories());
        if (request.getNote() != null) meal.setNote(request.getNote());
        if (request.getLoggedAt() != null) meal.setLoggedAt(request.getLoggedAt());
        if (request.getCategoryId() != null) {
            FoodCategory category = new FoodCategory();
            category.setId(request.getCategoryId());
            meal.setCategory(category);
        }
    }
}
