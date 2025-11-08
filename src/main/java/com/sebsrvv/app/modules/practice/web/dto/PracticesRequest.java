package com.sebsrvv.app.modules.practice.web.dto;


import lombok.Data;

@Data
public class PracticesRequest {
    private String name;
    private String description;
    private String icon;
    private String value_kind;
    private Double target_value;
    private String target_unit;
    private String practice_operator;
    private Integer days_per_week;
    private Boolean is_active;
}
