package com.sebsrvv.app.modules.goals.dto;

import jakarta.validation.constraints.*;

public record UpdateProgressDto(
        @NotNull @PositiveOrZero Double current_progress
) {}
