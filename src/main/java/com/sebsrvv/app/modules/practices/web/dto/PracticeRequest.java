package com.sebsrvv.app.modules.practices.web.dto;

import com.sebsrvv.app.modules.practices.domain.practice_operator;
import lombok.Data;

@Data
public class PracticeRequest {
    private String name;
    private String description;
    private String icon;
    private String value_kind;
    private Double target_value;
    private String target_unit;
    private practice_operator operator;  // Mantén como Enum
    private Integer days_per_week;
    private Boolean is_active;
}
