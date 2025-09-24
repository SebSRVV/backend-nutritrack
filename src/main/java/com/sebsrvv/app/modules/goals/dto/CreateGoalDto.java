package com.sebsrvv.app.modules.goals.dto;

import jakarta.validation.constraints.*;

public record CreateGoalDto(
        @NotBlank String goal_name,
        @NotBlank String goal_type,
        @NotNull @Positive Double target_value,
        @NotBlank String unit,
        Integer category_id
) {}
