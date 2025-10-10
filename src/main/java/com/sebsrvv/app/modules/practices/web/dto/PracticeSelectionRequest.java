// practices/web/dto/PracticeSelectionRequest.java
package com.sebsrvv.app.modules.practices.web.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.util.List;

public record PracticeSelectionRequest(
        @NotEmpty List<@Valid Item> selections
) {
    public record Item(
            @NotNull Integer defaultId,
            @NotNull Boolean active,
            @Min(0) @Max(7) Integer frequencyTarget
    ) {}
}
