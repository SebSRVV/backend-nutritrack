package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.FoodCategory;
import com.sebsrvv.app.modules.meals.domain.MealType;

import java.time.LocalDate;
import java.util.UUID;

public class MealRequest {
    private UUID userId;
    private LocalDate loggedAt;
    private MealType mealType;
    private FoodCategory foodCategory;
    private int calories;
    private String description;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public LocalDate getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDate loggedAt) { this.loggedAt = loggedAt; }

    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }

    public FoodCategory getFoodCategory() { return foodCategory; }
    public void setFoodCategory(FoodCategory foodCategory) { this.foodCategory = foodCategory; }

    public int getCalories() { return calories; }
    public void setCalories(int calories) { this.calories = calories; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
