// src/main/java/com/sebsrvv/app/modules/users/web/dto/IntakeVsGoalResponse.java
package com.sebsrvv.app.modules.users.web.dto;

import java.util.List;

public class IntakeVsGoalResponse {
    public int goalKcal;

    public static class Day {
        public String date;     // YYYY-MM-DD
        public int calories;
        public int delta;       // calories - goalKcal
    }
    public List<Day> days;

    public static class Summary {
        public int daysWithinGoal;   // calories <= goalKcal
        public double avgCalories;
        public double avgDelta;
    }
    public Summary summary;
}
