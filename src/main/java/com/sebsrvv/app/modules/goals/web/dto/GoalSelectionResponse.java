// modules/goals/web/dto/GoalSelectionResponse.java
package com.sebsrvv.app.modules.goals.web.dto;

public record GoalSelectionResponse(
        String id,
        Integer defaultId,
        String goalName,
        Integer weeklyTarget,
        Boolean active
) {}
