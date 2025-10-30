// src/main/java/com/sebsrvv/app/modules/goals/web/dto/GoalProgressRequest.java
package com.sebsrvv.app.modules.goals.web.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class GoalProgressRequest {
    //private UUID goal_id;
    private LocalDate log_date;
    private Integer value;
    private String note;
}
