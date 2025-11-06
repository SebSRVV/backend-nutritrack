package com.sebsrvv.app.modules.meals.web.dto;

import java.time.LocalDate;
import java.util.UUID;

public class FoodCategoryBreakdownRequest {
    private UUID userId;
    private LocalDate from;
    private LocalDate to;

    public UUID getUserId() { return userId; }
    public void setUserId(UUID userId) { this.userId = userId; }

    public LocalDate getFrom() { return from; }
    public void setFrom(LocalDate from) { this.from = from; }

    public LocalDate getTo() { return to; }
    public void setTo(LocalDate to) { this.to = to; }
}
