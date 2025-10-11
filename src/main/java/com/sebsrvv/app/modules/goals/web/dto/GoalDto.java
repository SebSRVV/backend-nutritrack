// dto/GoalDto.java
package com.sebsrvv.app.modules.goals.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoalDto(
        String id,
        String goal_name,
        String description,
        Integer weekly_target,   // 1..7
        Boolean is_active
) {}
