package com.sebsrvv.app.modules.practice.web.dto;

import lombok.Data;

import java.util.UUID;

public class ProgressResponse {
    private UUID practiceId;
    private Integer defaultId;
    private String practiceName;
    private Integer frequencyTarget;
    private Integer completionsThisWeek;
    private Integer remainingThisWeek;
    private Double progressPercent;
    private Boolean isActive;
    @Data
    public static class days {
        private String date;
        private Boolean completed;
        private int count;
    };

    private Integer streakCurrent;
    private Integer streakBest;
}
