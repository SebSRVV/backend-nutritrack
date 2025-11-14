// src/main/java/com/sebsrvv/app/modules/meals/web/dto/MealRequest.java
package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.MealType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.Instant;

@Data
public class MealRequest {

    @NotNull(message = "El tipo de comida es obligatorio.")
    private MealType mealType;

    @NotBlank(message = "La descripción es obligatoria.")
    private String description;

    @NotNull(message = "Las calorías son obligatorias.")
    @Positive(message = "Las calorías deben ser un número positivo.")
    private Double calories;

    @PositiveOrZero(message = "La cantidad de proteína no puede ser negativa.")
    private Double protein_g;

    @PositiveOrZero(message = "La cantidad de carbohidratos no puede ser negativa.")
    private Double carbs_g;

    @PositiveOrZero(message = "La cantidad de grasa no puede ser negativa.")
    private Double fat_g;

    @NotNull(message = "La fecha del registro es obligatoria.")
    private Instant loggedAt;
}
