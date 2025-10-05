package com.sebsrvv.app.modules.practice.web.dto;

import lombok.Data;

import java.util.UUID;

@Data
public class DailyResponse {
    private UUID id;
    private UUID userId;
    private UUID practiceId;
    private String LoggedAt;
    private String LoggedDate;
    private String Note;
    private String CreatedAt;
}
