// src/main/java/com/sebsrvv/app/modules/goals/web/dto/GoalProgressResponse.java
package com.sebsrvv.app.modules.goals.web.dto;

import lombok.Data;

import java.time.LocalDate;
import java.util.UUID;

@Data
public class GoalProgressResponse {
    private UUID id;
    private UUID goal_id;
    private LocalDate log_date;
    private Integer value;
    private String note;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }
    public UUID getGoal_id() { return goal_id; }
    public void setGoal_id(UUID goal_id) { this.goal_id = goal_id; }
    public LocalDate getLog_date() { return log_date; }
    public void setLog_date(LocalDate log_date) { this.log_date = log_date; }
    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }
    public String getNote() { return note; }
    public void setNote(String note) { this.note = note; }
}
