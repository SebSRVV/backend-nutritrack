// modules/goals/web/dto/GoalSelectionRequest.java
package com.sebsrvv.app.modules.goals.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record GoalSelectionRequest(
        @NotEmpty List<@Valid Item> selections
) {
    public record Item(@NotNull Integer defaultId, @NotNull Boolean active) {}
}
