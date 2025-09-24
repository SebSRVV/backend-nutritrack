package com.sebsrvv.app.modules.profile.dto;

public record RecommendationView(
        Integer age_years,
        Double bmr_kcal, Double tdee_kcal, Integer goal_kcal,
        Integer water_ml, Double activity_factor, Double diet_adjustment,
        Integer water_factor_ml_per_kg, Integer water_activity_bonus_ml,
        String method
) {}
