package com.sebsrvv.app.modules.metrics.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public record MetricsQuery(
        @NotNull
        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
        LocalDate dob,

        @NotNull
        @Positive
        Integer height_cm,

        @NotNull
        @Positive
        Integer weight_kg
) { }
