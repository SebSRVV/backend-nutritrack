// modules/goals/domain/model/GoalSelection.java
package com.sebsrvv.app.modules.goals.domain.model;

import java.util.UUID;

public class GoalSelection {
    private UUID id;
    private UUID userId;
    private Integer defaultId;
    private String goalName;
    private Integer weeklyTarget;
    private Boolean active;

    public static GoalSelection of(UUID id, UUID userId, Integer defaultId,
                                   String goalName, Integer weeklyTarget, Boolean active) {
        GoalSelection g = new GoalSelection();
        g.id = id; g.userId = userId; g.defaultId = defaultId;
        g.goalName = goalName; g.weeklyTarget = weeklyTarget; g.active = active;
        return g;
    }
    // getters/setters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public Integer getDefaultId() { return defaultId; }
    public String getGoalName() { return goalName; }
    public Integer getWeeklyTarget() { return weeklyTarget; }
    public Boolean getActive() { return active; }
    public void setGoalName(String goalName) { this.goalName = goalName; }
    public void setWeeklyTarget(Integer weeklyTarget) { this.weeklyTarget = weeklyTarget; }
}
