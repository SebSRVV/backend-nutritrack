package com.sebsrvv.app.modules.practices.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import java.util.UUID;

public record CheckPracticeDto(
        @NotNull UUID practice_id,
        @NotNull LocalDate date
) {}
