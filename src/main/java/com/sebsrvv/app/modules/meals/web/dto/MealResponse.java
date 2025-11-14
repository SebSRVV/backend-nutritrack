// src/main/java/com/sebsrvv/app/modules/meals/web/dto/MealResponse.java
package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.MealType;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

/**
 * DTO de salida para devolver al cliente.
 * Uso de tipos envoltorio (Double) para evitar problemas con nulls.
 */
@Data
public class MealResponse {

    private UUID id;
    private MealType mealType;
    private String description;
    private Double calories;
    private Double protein_g;
    private Double carbs_g;
    private Double fat_g;
    private Instant loggedAt;
}
