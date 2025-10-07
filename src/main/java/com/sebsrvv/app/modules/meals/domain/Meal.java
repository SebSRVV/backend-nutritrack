package com.sebsrvv.app.modules.meals.domain;

import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class Meal {
    private UUID id;
    private UUID userId;
    private MealType mealType;
    private String description;
    private Integer calories;
    private Double proteinG;
    private Double carbsG;
    private Double fatG;
    private Instant loggedAt;
    private Instant createdAt;
    private List<MealCategory> categories;
    private String note;
}
