// web/dto/PracticeLogRequest.java
package com.sebsrvv.app.modules.practices.web.dto;

import jakarta.validation.constraints.NotNull;

public record PracticeLogRequest(
        // ISO-8601; si es null, se usa "ahora"
        String loggedAt,
        String note
) {}
