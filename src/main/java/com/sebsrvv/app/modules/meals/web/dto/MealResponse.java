package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.FoodCategory;
import com.sebsrvv.app.modules.meals.domain.MealType;

import java.time.LocalDate;
import java.util.UUID;

public class MealResponse {
    private UUID id;
    private UUID userId;
    private LocalDate loggedAt;
    private MealType mealType;
    private FoodCategory foodCategory;
    private int calories;
    private String description;

    public MealResponse(UUID id, UUID userId, LocalDate loggedAt, MealType mealType,
                        FoodCategory foodCategory, int calories, String description) {
        this.id = id;
        this.userId = userId;
        this.loggedAt = loggedAt;
        this.mealType = mealType;
        this.foodCategory = foodCategory;
        this.calories = calories;
        this.description = description;
    }

    // Getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public LocalDate getLoggedAt() { return loggedAt; }
    public MealType getMealType() { return mealType; }
    public FoodCategory getFoodCategory() { return foodCategory; }
    public int getCalories() { return calories; }
    public String getDescription() { return description; }
}
