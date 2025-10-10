// modules/practices/web/dto/PracticeUpdateResponse.java
package com.sebsrvv.app.modules.practices.web.dto;

public record PracticeUpdateResponse(
        String id,
        String practiceName,
        String description,
        String icon,
        Integer frequencyTarget,
        Boolean isActive,
        String updatedAt
) {}
