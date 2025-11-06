package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.MealType;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para responder al cliente con la información de un Meal.
 */
@Data
public class MealResponse {

    private UUID id;
    private UUID userId;
    private String name;
    private MealType mealType;
    private Double calories;
    private String note;
    private LocalDate loggedAt;
    private UUID categoryId; // Cambiado a UUID para mantener consistencia con la entidad
}
