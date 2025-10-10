// practices/web/dto/PracticeSelectionResponse.java
package com.sebsrvv.app.modules.practices.web.dto;

import java.util.UUID;

public record PracticeSelectionResponse(
        UUID id,
        Integer defaultId,
        String practiceName,
        String description,
        String icon,
        Integer frequencyTarget,
        Boolean isActive,
        Integer sortOrder
) {}
