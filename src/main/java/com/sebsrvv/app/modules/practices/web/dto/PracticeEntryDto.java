// src/main/java/com/sebsrvv/app/practices/dto/PracticeEntryDto.java
package com.sebsrvv.app.modules.practices.web.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record PracticeEntryDto(
        String id,           // uuid
        String practice_id,  // uuid
        String log_date,     // "YYYY-MM-DD"
        Double value,        // 1/0 para booleano
        String note,
        Boolean achieved
) {}
