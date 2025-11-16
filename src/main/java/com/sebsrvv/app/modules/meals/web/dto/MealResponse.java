package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.MealType;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class MealResponse {
    private Long id;
    private MealType mealType;    // la API devuelve enum al cliente
    private String description;
    private Double calories;
    private Double protein_g;
    private Double carbs_g;
    private Double fat_g;
    private LocalDate loggedAt;   // la API entrega LocalDate (no Instant)
    private List<String> mealItems;
}
