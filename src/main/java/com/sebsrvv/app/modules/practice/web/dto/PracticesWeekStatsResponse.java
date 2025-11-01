package com.sebsrvv.app.modules.practice.web.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PracticesWeekStatsResponse {
    private String name;
    private Short daysPerWeek;
    private Long achievedDaysLast7;
    private Long loggedDaysLast7;
    private LocalDate firstLogInRange;
    private LocalDate lastLogInRange = LocalDate.now();
}
