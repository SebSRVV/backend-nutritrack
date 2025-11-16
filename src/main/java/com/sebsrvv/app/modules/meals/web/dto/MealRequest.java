package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.MealType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class MealRequest {

    @NotNull(message = "El tipo de comida es obligatorio.")
    private MealType mealType;

    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @NotNull(message = "Las calorías son obligatorias.")
    @Positive(message = "Las calorías deben ser un número positivo.")
    private Double calories;

    @PositiveOrZero
    private Double protein_g = 0.0;

    @PositiveOrZero
    private Double carbs_g = 0.0;

    @PositiveOrZero
    private Double fat_g = 0.0;

    @NotNull(message = "La fecha del registro es obligatoria.")
    private LocalDate loggedAt; // la API usa LocalDate, mapper convierte a Instant
}
