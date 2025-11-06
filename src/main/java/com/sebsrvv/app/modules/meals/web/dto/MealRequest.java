package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.MealType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

/**
 * DTO para crear o actualizar un Meal.
 */
@Data
public class MealRequest {

    @NotNull(message = "El ID de usuario no puede ser nulo.")
    private UUID userId;

    @NotBlank(message = "El nombre de la comida es obligatorio.")
    private String name;

    @NotNull(message = "El tipo de comida es obligatorio.")
    private MealType mealType;

    @NotNull(message = "Las calorías son obligatorias.")
    @Positive(message = "Las calorías deben ser un número positivo.")
    private Double calories;

    private String note; // Campo opcional

    @NotNull(message = "La fecha del registro es obligatoria.")
    private LocalDate loggedAt;

    private UUID categoryId; // Cambiado a UUID para la relación con FoodCategory
}
