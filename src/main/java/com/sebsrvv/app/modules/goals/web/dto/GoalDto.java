package com.sebsrvv.app.modules.goals.web.dto;

public record GoalDto(
        String goal_name,        // text NOT NULL
        String description,      // text
        Integer weekly_target,   // integer CHECK 1..7
        Boolean is_active,       // boolean
        Integer category_id      // integer (FK food_categories)
) {}
