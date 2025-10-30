// src/main/java/com/sebsrvv/app/modules/goals/web/dto/GoalResponse.java
package com.sebsrvv.app.modules.goals.web.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Data
public class GoalResponse {
    private UUID id;
    private String goal_name;
    private String description;
    private Integer weekly_target;
    private Boolean is_active;
    private Integer category_id;
    private String value_type;
    private String unit;
    private LocalDate start_date;
    private LocalDate end_date;
    private BigDecimal target_value;
    private OffsetDateTime created_at;
    private OffsetDateTime updated_at;
}
