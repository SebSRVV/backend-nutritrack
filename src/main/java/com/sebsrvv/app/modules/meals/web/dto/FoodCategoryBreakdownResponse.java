package com.sebsrvv.app.modules.meals.web.dto;

import lombok.Data;

@Data
public class FoodCategoryBreakdownResponse {
    private Integer categoryId;
    private String name;
    private Integer count;    // cantidad de comidas asociadas
    private Integer calories; // total de calorías
}
