// src/main/java/com/sebsrvv/app/modules/users/web/dto/GoalsWeeklyItem.java
package com.sebsrvv.app.modules.users.web.dto;

import java.util.UUID;

public class GoalsWeeklyItem {
    public UUID goalId;
    public String goalName;
    public int weeklyTarget;
    public int completedThisWeek;
    public double progressPercent;
}
