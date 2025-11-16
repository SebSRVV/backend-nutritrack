package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.MealType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@NoArgsConstructor
public class MealResponse {
    private Long id;
    private MealType mealType;
    private String description;
    private Double calories;
    private Double protein_g;
    private Double carbs_g;
    private Double fat_g;
    private Instant loggedAt;
    private List<String> mealItems;
}
