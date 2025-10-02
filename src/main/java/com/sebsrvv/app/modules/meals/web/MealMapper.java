package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;

import java.util.stream.Collectors;

public class MealMapper {

    public static MealResponse toResponse(Meal meal) {
        MealResponse response = new MealResponse();
        response.setId(meal.getId());
        response.setUserId(meal.getUserId());
        response.setMealType(meal.getMealType().name().toLowerCase());
        response.setDescription(meal.getDescription());
        response.setCalories(meal.getCalories());
        response.setProteinG(meal.getProteinG());
        response.setCarbsG(meal.getCarbsG());
        response.setFatG(meal.getFatG());
        response.setLoggedAt(meal.getLoggedAt());
        response.setCreatedAt(meal.getCreatedAt());
        response.setNote(meal.getNote());

        if (meal.getCategories() != null) {
            response.setCategories(meal.getCategories().stream().map(c -> {
                MealResponse.CategoryDto dto = new MealResponse.CategoryDto();
                dto.setId(c.getId());
                dto.setName(c.getName());
                return dto;
            }).collect(Collectors.toList()));
        }
        return response;
    }
}
