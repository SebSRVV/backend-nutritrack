// modules/goals/web/dto/GoalUpdateRequest.java
package com.sebsrvv.app.modules.goals.web.dto;

public record GoalUpdateRequest(
        String goalName,
        Integer weeklyTarget,
        Boolean isActive,
        Integer categoryId,
        String description
) {}
