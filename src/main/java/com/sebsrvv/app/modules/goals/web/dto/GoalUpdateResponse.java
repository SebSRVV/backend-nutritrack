// modules/goals/web/dto/GoalUpdateResponse.java
package com.sebsrvv.app.modules.goals.web.dto;

public record GoalUpdateResponse(
        String id,
        String userId,
        Integer defaultId,
        String goalName,
        Integer weeklyTarget,
        Boolean isActive,
        Integer categoryId,
        String description,
        String updatedAt
) {}
