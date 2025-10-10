// modules/practices/web/dto/PracticeProgressResponse.java
package com.sebsrvv.app.modules.practices.web.dto;

import java.util.List;

public record PracticeProgressResponse(
        String practiceId,
        Integer defaultId,
        String practiceName,
        Integer frequencyTarget,
        Integer completionsThisWeek,
        Integer remainingThisWeek,
        Double progressPercent,
        Boolean isActive,
        List<Day> days,
        Integer streakCurrent,
        Integer streakBest
) {
    public record Day(String date, boolean completed, int count) {}
}
