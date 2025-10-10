// modules/goals/web/dto/GoalProgressResponse.java
package com.sebsrvv.app.modules.goals.web.dto;

public record GoalProgressResponse(
        String id,
        String userId,
        String goalId,
        String logDate,
        Integer value,
        String note,
        String createdAt,
        String updatedAt
) {}
