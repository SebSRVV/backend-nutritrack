package com.sebsrvv.app.modules.meals.web.dto;

import com.sebsrvv.app.modules.meals.domain.MealType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class CreateMealDTO {

    @NotNull(message = "El ID del usuario es obligatorio.")
    private UUID userId;

    @NotBlank(message = "El nombre de la comida no puede estar vacío.")
    private String name;

    @NotNull(message = "Debe especificar el tipo de comida.")
    private MealType mealType;

    @NotNull(message = "Debe ingresar las calorías.")
    @Positive(message = "Las calorías deben ser un número positivo.")
    private Double calories;

    private String note;

    @NotNull(message = "Debe especificar la fecha del registro.")
    private LocalDate loggedAt;

    private UUID categoryId;
}
