// src/main/java/com/sebsrvv/app/practices/dto/PracticeDto.java
package com.sebsrvv.app.modules.practices.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PracticeDto(
        String id,          // uuid
        String name,
        String description,
        String icon,
        String value_kind,  // "boolean" | "quantity"
        Double target_value,
        String target_unit,
        String operator,    // "gte" | "lte" | "eq"
        Integer days_per_week,
        Boolean is_active
) {}
