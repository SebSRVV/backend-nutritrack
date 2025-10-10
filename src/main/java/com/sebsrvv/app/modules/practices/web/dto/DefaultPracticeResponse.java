// web/dto/DefaultPracticeResponse.java
package com.sebsrvv.app.modules.practices.web.dto;

public record DefaultPracticeResponse(
        Integer id,
        String practiceName,
        String description,
        String icon,
        Integer frequencyTarget,
        Boolean isActive
) {}
