package com.sebsrvv.app.modules.goals.domain;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class GoalProgress {
    private UUID id;
    private UUID goalId;
    private LocalDate logDate;   // YYYY-MM-DD
    private Integer value;       // 0 | 1
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGoalId() { return goalId; }
    public void setGoalId(UUID goalId) { this.goalId = goalId; }
    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
