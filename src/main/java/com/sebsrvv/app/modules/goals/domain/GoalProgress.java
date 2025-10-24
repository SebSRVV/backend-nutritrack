package com.sebsrvv.app.modules.goals.domain;

public class GoalProgress {
    private String id;
    private String userId;
    private String goalId;
    private String logDate; // YYYY-MM-DD
    private Integer value;  // 0 | 1
    private String note;
    private String createdAt;
    private String updatedAt;

    // getters & setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }
    public String getGoalId() { return goalId; }
    public void setGoalId(String goalId) { this.goalId = goalId; }
    public String getLogDate() { return logDate; }
    public void setLogDate(String logDate) { this.logDate = logDate; }
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public String getCreatedAt() { return createdAt; }
    public void setCreatedAt(String createdAt) { this.createdAt = createdAt; }
    public String getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(String updatedAt) { this.updatedAt = updatedAt; }
}
