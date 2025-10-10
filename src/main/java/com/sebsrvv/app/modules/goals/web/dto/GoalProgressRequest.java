// modules/goals/web/dto/GoalProgressRequest.java
package com.sebsrvv.app.modules.goals.web.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public record GoalProgressRequest(
        @NotNull
        @JsonAlias({"logDate","log_date"})
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
        LocalDate logDate,          // YYYY-MM-DD

        @NotNull @Min(0) @Max(1)
        Integer value,              // 0 | 1

        @Size(max = 2000)
        String note
) {}
