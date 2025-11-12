package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.MealType;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class MealResponse {

    private UUID id; // opcional, si tu entidad tiene un identificador
    private MealType mealType;
    private String description;
    private double calories;
    private double protein_g;
    private double carbs_g;
    private double fat_g;
    private LocalDateTime loggedAt;
}
