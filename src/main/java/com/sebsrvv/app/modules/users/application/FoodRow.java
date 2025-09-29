// src/main/java/com/sebsrvv/app/modules/users/application/FoodRow.java
package com.sebsrvv.app.modules.users.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.LocalDate;

public class FoodRow {

    @JsonProperty("period_start")
    public LocalDate periodStart;

    @JsonProperty("period_end")
    public LocalDate periodEnd;

    @JsonProperty("category_id")
    public Integer categoryId;

    @JsonProperty("category_name")
    public String categoryName;

    @JsonProperty("items_count")
    public Long itemsCount;

    @JsonProperty("total_calories")
    public Long totalCalories;

    @JsonProperty("total_protein_g")
    public BigDecimal totalProteinG;

    @JsonProperty("total_carbs_g")
    public BigDecimal totalCarbsG;

    @JsonProperty("total_fat_g")
    public BigDecimal totalFatG;
}
