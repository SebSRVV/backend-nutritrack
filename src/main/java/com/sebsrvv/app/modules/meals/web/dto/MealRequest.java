package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.MealType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.List;

public class MealRequest {

    @NotNull
    private MealType mealType;

    @NotBlank(message = "description no puede estar vacío")
    private String description;

    private Double calories;
    private Double protein_g;
    private Double carbs_g;
    private Double fat_g;

    // LocalDate expected as "yyyy-MM-dd"
    private LocalDate loggedAt;

    private List<String> mealItems;

    public MealRequest() {}

    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getProtein_g() { return protein_g; }
    public void setProtein_g(Double protein_g) { this.protein_g = protein_g; }

    public Double getCarbs_g() { return carbs_g; }
    public void setCarbs_g(Double carbs_g) { this.carbs_g = carbs_g; }

    public Double getFat_g() { return fat_g; }
    public void setFat_g(Double fat_g) { this.fat_g = fat_g; }

    public LocalDate getLoggedAt() { return loggedAt; }
    public void setLoggedAt(LocalDate loggedAt) { this.loggedAt = loggedAt; }

    public List<String> getMealItems() { return mealItems; }
    public void setMealItems(List<String> mealItems) { this.mealItems = mealItems; }
}
