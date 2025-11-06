package com.sebsrvv.app.modules.meals.web;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.web.dto.MealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class MealMapper {

    public Meal toEntity(MealRequest request) {
        Meal meal = new Meal();
        meal.setUserId(request.getUserId());
        meal.setLoggedAt(request.getLoggedAt());
        meal.setMealType(request.getMealType());
        meal.setFoodCategory(request.getFoodCategory());
        meal.setCalories(request.getCalories());
        meal.setDescription(request.getDescription());
        return meal;
    }

    public MealResponse toResponse(Meal meal) {
        return new MealResponse(
                meal.getId(),
                meal.getUserId(),
                meal.getLoggedAt(),
                meal.getMealType(),
                meal.getFoodCategory(),
                meal.getCalories(),
                meal.getDescription()
        );
    }

    public List<MealResponse> toResponseList(List<Meal> meals) {
        return meals.stream().map(this::toResponse).collect(Collectors.toList());
    }
}
