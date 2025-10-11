// dto/GoalProgressDto.java
package com.sebsrvv.app.modules.goals.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoalProgressDto(
        String id,
        String goal_id,
        String log_date,   // YYYY-MM-DD
        Integer value,     // 0 o 1
        String note
) {}
