// modules/practices/web/dto/PracticeSingleProgressResponse.java
package com.sebsrvv.app.modules.practices.web.dto;

import java.util.List;

public record PracticeSingleProgressResponse(
        String practiceId,
        String practiceName,
        Integer frequencyTarget,
        Integer completionsThisWeek,
        List<Day> days,
        Double progressPercent
) {
    public record Day(String date, boolean completed, List<Log> logs) {}
    public record Log(String id, String loggedAt) {}
}
