package com.sebsrvv.app.modules.users.web.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class NutritionReportRequest {

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate from;

    @NotNull
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate to;

    @Valid
    @NotNull
    private Include include = new Include();

    @AssertTrue(message = "'from' must be <= 'to'")
    public boolean isValidRange() {
        return from == null || to == null || !from.isAfter(to);
    }

    public static class Include {
        private boolean foodByCategory = true;
        private boolean intakeVsGoal   = true;
        private boolean trends         = true;
        private boolean notes          = true;

        public boolean isFoodByCategory() { return foodByCategory; }
        public void setFoodByCategory(boolean foodByCategory) { this.foodByCategory = foodByCategory; }
        public boolean isIntakeVsGoal() { return intakeVsGoal; }
        public void setIntakeVsGoal(boolean intakeVsGoal) { this.intakeVsGoal = intakeVsGoal; }
        public boolean isTrends() { return trends; }
        public void setTrends(boolean trends) { this.trends = trends; }
        public boolean isNotes() { return notes; }
        public void setNotes(boolean notes) { this.notes = notes; }
    }

    public LocalDate getFrom() { return from; }
    public void setFrom(LocalDate from) { this.from = from; }
    public LocalDate getTo() { return to; }
    public void setTo(LocalDate to) { this.to = to; }
    public Include getInclude() { return include; }
    public void setInclude(Include include) { this.include = include; }
}
