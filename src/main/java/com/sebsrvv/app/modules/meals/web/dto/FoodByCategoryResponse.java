package com.sebsrvv.app.modules.meals.web.dto;

import lombok.Data;

@Data
public class FoodByCategoryResponse {
    private Integer categoryId;
    private String name;
    private Integer count;
    private Integer calories;
}
