package com.sebsrvv.app.modules.goals.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoalProgressDto(
        String goal_id, // UUID
        String log_date, // YYYY-MM-DD
        Integer value,   // 0 | 1 (tu BD lo tiene int 0/1)
        String note
) {}
