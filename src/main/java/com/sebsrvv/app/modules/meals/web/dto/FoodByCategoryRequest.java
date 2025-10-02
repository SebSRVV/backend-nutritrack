package com.sebsrvv.app.modules.meals.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record FoodByCategoryRequest(

        @NotBlank(message = "La fecha inicial no puede estar vacía")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "El formato de fecha debe ser YYYY-MM-DD")
        String from,

        @NotBlank(message = "La fecha final no puede estar vacía")
        @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "El formato de fecha debe ser YYYY-MM-DD")
        String to,

        @NotBlank(message = "El campo groupBy es obligatorio")
        @Pattern(regexp = "day|week|month", message = "groupBy solo puede ser: day, week o month")
        String groupBy
) {}
