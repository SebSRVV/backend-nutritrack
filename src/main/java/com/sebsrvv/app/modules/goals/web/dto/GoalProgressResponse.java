// modules/goals/web/dto/GoalProgressResponse.java
package com.sebsrvv.app.modules.goals.web.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.Instant;
import java.time.LocalDate;

public record GoalProgressResponse(
        String id,
        @JsonProperty("user_id") String userId,
        @JsonProperty("goal_id") String goalId,
        @JsonProperty("log_date") LocalDate logDate,
        Integer value,
        String note,
        @JsonProperty("created_at") Instant createdAt,
        @JsonProperty("updated_at") Instant updatedAt
) {}
