package com.sebsrvv.app.modules.meals.web.dto;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class MealRequest {
    @NotBlank
    private String mealType;

    @NotBlank
    private String description;

    @Min(0)
    private int calories;

    @DecimalMin("0.0")
    private double proteinG;

    @DecimalMin("0.0")
    private double carbsG;

    @DecimalMin("0.0")
    private double fatG;

    @NotNull
    private Instant loggedAt;

    private List<Integer> categoryIds;
    private List<String> categories;
    private String note;
}
