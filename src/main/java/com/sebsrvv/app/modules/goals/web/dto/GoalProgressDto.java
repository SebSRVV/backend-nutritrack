package com.sebsrvv.app.modules.goals.web.dto;

public record GoalProgressDto(
        String goal_id,   // uuid (string)
        String log_date,  // "YYYY-MM-DD" (date)
        Integer value,    // 0 | 1
        String note       // text
) {}
