package com.sebsrvv.app.modules.meals.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class FoodByCategoryRequest {
    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    private String from;

    @NotBlank
    @Pattern(regexp = "\\d{4}-\\d{2}-\\d{2}")
    private String to;

    @NotBlank
    @Pattern(regexp = "day|week|month")
    private String groupBy;
}
