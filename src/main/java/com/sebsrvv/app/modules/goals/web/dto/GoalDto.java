package com.sebsrvv.app.modules.goals.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import java.math.BigDecimal;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record GoalDto(
        String goal_name,
        String description,
        Integer weekly_target,
        Boolean is_active,
        Integer category_id,

        // NUEVOS (opcionales, calzan con columnas reales de la BD)
        String value_type,      // 'BOOLEAN' | 'QUANTITATIVE' (texto)
        String unit,            // 'bool','g','ml','kcal','portion','count'
        String start_date,      // YYYY-MM-DD
        String end_date,        // YYYY-MM-DD
        BigDecimal target_value // objetivo cuantificable opcional
) {}
