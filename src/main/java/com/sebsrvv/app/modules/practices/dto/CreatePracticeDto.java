package com.sebsrvv.app.modules.practices.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePracticeDto(
        @NotBlank String practice_name,
        String description,
        String icon,
        Integer frequency_target
) {}
