package com.sebsrvv.app.modules.meals.web.dto;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FoodCategoryBreakdownRequest {
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "El formato de fecha debe ser YYYY-MM-DD")
    private String from; // fecha inicial opcional

    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}", message = "El formato de fecha debe ser YYYY-MM-DD")
    private String to;   // fecha final opcional
}
