package com.sebsrvv.app.modules.practice.web.dto;

import lombok.Data;

import java.util.UUID;
@Data
public class WeekStartResponse {
    private UUID practiceId;
    private String practiceName;
    private Integer frequencyTarget;
    private Integer completionsThisWeek;
    @Data
    public static class days {
        private String date;
        private Boolean completed;
        private int count;
        @Data
        public static class logs {
            private UUID id;
            private String LoggedAt;
        };
    };
    private Double progressPercent;
}
