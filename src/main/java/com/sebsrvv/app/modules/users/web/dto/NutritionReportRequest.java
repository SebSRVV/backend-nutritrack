package com.sebsrvv.app.modules.users.web.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public class NutritionReportRequest {

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "from must be YYYY-MM-DD")
    private String from;

    @NotNull
    @Pattern(regexp = "^\\d{4}-\\d{2}-\\d{2}$", message = "to must be YYYY-MM-DD")
    private String to;

    @NotNull
    private Include include = new Include();

    public static class Include {
        private boolean foodByCategory;
        private boolean intakeVsGoal;
        private boolean trends;
        private boolean notes;

        public boolean isFoodByCategory() { return foodByCategory; }
        public void setFoodByCategory(boolean foodByCategory) { this.foodByCategory = foodByCategory; }
        public boolean isIntakeVsGoal() { return intakeVsGoal; }
        public void setIntakeVsGoal(boolean intakeVsGoal) { this.intakeVsGoal = intakeVsGoal; }
        public boolean isTrends() { return trends; }
        public void setTrends(boolean trends) { this.trends = trends; }
        public boolean isNotes() { return notes; }
        public void setNotes(boolean notes) { this.notes = notes; }
    }

    public String getFrom() { return from; }
    public void setFrom(String from) { this.from = from; }
    public String getTo() { return to; }
    public void setTo(String to) { this.to = to; }
    public Include getInclude() { return include; }
    public void setInclude(Include include) { this.include = include; }
}
