package com.sebsrvv.app.modules.practice.web.dto;

import lombok.Data;

@Data
public class PracticesWeekStatsRequest {
    private String name;
    private Short daysPerWeek;
    private Long achievedDaysLast7;
    private Long loggedDaysLast7;
}
