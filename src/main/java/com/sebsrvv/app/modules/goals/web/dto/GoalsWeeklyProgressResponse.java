// modules/goals/web/dto/GoalsWeeklyProgressResponse.java
package com.sebsrvv.app.modules.goals.web.dto;

public record GoalsWeeklyProgressResponse(
        String goalId,
        Integer defaultId,
        String goalName,
        Integer weeklyTarget,
        Integer completedThisWeek,
        Integer remainingThisWeek,
        Double progressPercent,
        Boolean isActive,
        Integer streakCurrent,
        Integer streakBest
) {}
