// src/main/java/com/sebsrvv/app/modules/users/application/GoalWeeklyRow.java
package com.sebsrvv.app.modules.users.application;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.UUID;

public class GoalWeeklyRow {
    @JsonProperty("goal_id")
    public UUID goalId;

    @JsonProperty("goal_name")
    public String goalName;

    @JsonProperty("weekly_target")
    public Integer weeklyTarget;

    @JsonProperty("completed_this_week")
    public Integer completedThisWeek;

    @JsonProperty("progress_percent")
    public Double progressPercent;
}
