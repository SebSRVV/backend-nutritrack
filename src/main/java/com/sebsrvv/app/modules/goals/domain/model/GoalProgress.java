// modules/goals/domain/model/GoalProgress.java
package com.sebsrvv.app.modules.goals.domain.model;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

public class GoalProgress {
    private UUID id;
    private UUID userId;
    private UUID goalId;
    private LocalDate logDate;
    private Integer value; // 0 | 1
    private String note;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    public GoalProgress(UUID id, UUID userId, UUID goalId, LocalDate logDate, Integer value,
                        String note, OffsetDateTime createdAt, OffsetDateTime updatedAt) {
        this.id = id; this.userId = userId; this.goalId = goalId;
        this.logDate = logDate; this.value = value; this.note = note;
        this.createdAt = createdAt; this.updatedAt = updatedAt;
    }

    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getGoalId() { return goalId; }
    public LocalDate getLogDate() { return logDate; }
    public Integer getValue() { return value; }
    public String getNote() { return note; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
