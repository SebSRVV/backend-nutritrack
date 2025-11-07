// src/main/java/com/sebsrvv/app/modules/goals/domain/GoalProgress.java
package com.sebsrvv.app.modules.goals.domain;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "user_goal_progress")
public class GoalProgress {

    @Id
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID id; // lo genera Postgres

    @Column(name = "user_id", nullable = false, updatable = false)
    private UUID userId;

    @Column(name = "goal_id", nullable = false)
    private UUID goalId;

    @Column(name = "log_date", nullable = false)
    private LocalDate logDate;

    @Column(name = "value", nullable = false)
    private Integer value; // 0 | 1

    @Column(name = "note")
    private String note;

    @Column(name = "value_num", insertable = false, updatable = false)
    private BigDecimal valueNum;

    @Column(name = "created_at", insertable = false, updatable = false)
    private OffsetDateTime createdAt;

    @Column(name = "updated_at", insertable = false, updatable = false)
    private OffsetDateTime updatedAt;

    // Getters/Setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public UUID getGoalId() { return goalId; }
    public void setGoalId(UUID goalId) { this.goalId = goalId; }

    public LocalDate getLogDate() { return logDate; }
    public void setLogDate(LocalDate logDate) { this.logDate = logDate; }

    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }

    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }

    public BigDecimal getValueNum() { return valueNum; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
}
