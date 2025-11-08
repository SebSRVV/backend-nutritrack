// src/main/java/com/sebsrvv/app/modules/goals/web/dto/GoalRequest.java
package com.sebsrvv.app.modules.goals.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class GoalRequest {
    private String goal_name;
    private String description;
    private Integer weekly_target;
    private Boolean is_active;
    private Integer category_id;
    private String value_type;   // 'BOOLEAN' | 'QUANTITATIVE'
    private String unit;         // 'bool'|'g'|'ml'|'kcal'|'portion'|'count'
    private LocalDate start_date;
    private LocalDate end_date;
    private BigDecimal target_value;
}
