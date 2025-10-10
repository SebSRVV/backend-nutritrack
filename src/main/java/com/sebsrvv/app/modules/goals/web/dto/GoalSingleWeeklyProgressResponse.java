// modules/goals/web/dto/GoalSingleWeeklyProgressResponse.java
package com.sebsrvv.app.modules.goals.web.dto;

import java.util.List;

public record GoalSingleWeeklyProgressResponse(
        String goalId,
        Integer defaultId,
        String goalName,
        Integer weeklyTarget,
        List<Day> days,
        Integer completedThisWeek,
        Integer remainingThisWeek,
        Double progressPercent,
        Boolean isActive,
        Integer streakCurrent,
        Integer streakBest
) {
    public record Day(String date, Integer value, String note) {}
}
