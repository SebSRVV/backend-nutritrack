package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.MealLogCategory;

public class FoodCategoryBreakdownResponse {
    private MealLogCategory category;
    private long totalCalories;

    public FoodCategoryBreakdownResponse(MealLogCategory category, long totalCalories) {
        this.category = category;
        this.totalCalories = totalCalories;
    }

    public MealLogCategory getCategory() { return category; }
    public long getTotalCalories() { return totalCalories; }
}
