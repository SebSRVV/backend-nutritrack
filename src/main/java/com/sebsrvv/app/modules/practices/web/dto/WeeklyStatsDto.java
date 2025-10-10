// src/main/java/com/sebsrvv/app/practices/dto/WeeklyStatsDto.java
package com.sebsrvv.app.modules.practices.web.dto;

public record WeeklyStatsDto(
        String practice_id,
        String name,
        Integer days_per_week,
        Integer achieved_days_last7,
        Integer logged_days_last7
) {}
