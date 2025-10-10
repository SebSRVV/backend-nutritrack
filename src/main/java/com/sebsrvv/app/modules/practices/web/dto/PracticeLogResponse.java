// web/dto/PracticeLogResponse.java
package com.sebsrvv.app.modules.practices.web.dto;

public record PracticeLogResponse(
        String id,
        String userId,
        String practiceId,
        String loggedAt,
        String loggedDate,
        String note,
        String createdAt
) {}
