// src/main/java/com/sebsrvv/app/modules/users/web/dto/MetricsResponse.java
package com.sebsrvv.app.modules.metrics.web.dto;

public record MetricsResponse(Double bmi, Integer age, Integer daysToBirthday) {}