package com.sebsrvv.app.modules.metrics.web.dto;

public record MetricsResponse(
        Double bmi,
        Integer age,
        Integer daysToBirthday
) { }
