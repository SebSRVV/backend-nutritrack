package com.sebsrvv.app.modules.goals.dto;

import java.util.UUID;

public record GoalView(
        UUID id,
        String goal_name,
        String goal_type,
        Double target_value,
        Double current_progress,
        String unit,
        Integer category_id,
        String category_name
) {}
