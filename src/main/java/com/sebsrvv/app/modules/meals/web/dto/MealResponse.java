package com.sebsrvv.app.modules.meals.web.dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
public class MealResponse {
    private UUID id;
    private UUID userId;
    private String mealType;
    private String description;
    private int calories;
    private double proteinG;
    private double carbsG;
    private double fatG;
    private Instant loggedAt;
    private Instant createdAt;
    private List<CategoryDto> categories;
    private String note;

    @Data
    public static class CategoryDto {
        private Integer id;
        private String name;
    }
}
