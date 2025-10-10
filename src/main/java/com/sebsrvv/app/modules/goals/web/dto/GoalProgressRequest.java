// modules/goals/web/dto/GoalProgressRequest.java
package com.sebsrvv.app.modules.goals.web.dto;

public record GoalProgressRequest(
        String logDate, // YYYY-MM-DD
        Integer value,  // 0 | 1
        String note
) {}
