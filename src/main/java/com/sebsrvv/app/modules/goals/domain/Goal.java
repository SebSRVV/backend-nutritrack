package com.sebsrvv.app.modules.goals.domain;

import java.time.OffsetDateTime;
import java.util.UUID;

public class Goal {
    private UUID id;
    private String goalName;
    private Integer categoryId; // FK food_categories
    private Integer weeklyTarget; // 1..7
    private Boolean isActive;
    private String description;
    private OffsetDateTime createdAt;
    private OffsetDateTime updatedAt;

    // getters & setters
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public String getGoalName() { return goalName; }
    public void setGoalName(String goalName) { this.goalName = goalName; }
    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }
    public Integer getWeeklyTarget() { return weeklyTarget; }
    public void setWeeklyTarget(Integer weeklyTarget) { this.weeklyTarget = weeklyTarget; }
    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean active) { isActive = active; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
    public OffsetDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(OffsetDateTime updatedAt) { this.updatedAt = updatedAt; }
}
