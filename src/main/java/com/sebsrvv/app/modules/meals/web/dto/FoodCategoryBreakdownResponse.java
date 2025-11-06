package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.FoodCategory;

public class FoodCategoryBreakdownResponse {
    private FoodCategory category;
    private long totalCalories;

    public FoodCategoryBreakdownResponse(FoodCategory category, long totalCalories) {
        this.category = category;
        this.totalCalories = totalCalories;
    }

    public FoodCategory getCategory() { return category; }
    public long getTotalCalories() { return totalCalories; }
}
