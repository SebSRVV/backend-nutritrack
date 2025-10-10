// modules/practices/web/dto/PracticeUpdateRequest.java
package com.sebsrvv.app.modules.practices.web.dto;

public record PracticeUpdateRequest(
        String practiceName,
        String description,
        String icon,
        Integer frequencyTarget,
        Boolean isActive
) {}
