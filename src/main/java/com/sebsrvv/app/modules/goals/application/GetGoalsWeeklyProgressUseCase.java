// modules/goals/application/GetGoalsWeeklyProgressUseCase.java
package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.port.GoalsWeeklyProgressPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class GetGoalsWeeklyProgressUseCase {

    private final GoalsWeeklyProgressPort port;

    public GetGoalsWeeklyProgressUseCase(GoalsWeeklyProgressPort port) {
        this.port = port;
    }

    public List<Result> execute(UUID userId, LocalDate weekStart, LocalDate weekEnd) {
        if (weekStart == null || weekEnd == null || weekEnd.isBefore(weekStart))
            throw new IllegalArgumentException("Rango inválido (weekStart <= weekEnd)");

        var goals = port.findUserGoals(userId);
        if (goals.isEmpty()) return List.of();

        var ids = goals.stream().map(GoalsWeeklyProgressPort.UserGoal::id).toList();
        var values = port.findDailyValues(userId, ids, weekStart, weekEnd);

        List<Result> out = new ArrayList<>();
        for (var g : goals) {
            Map<LocalDate, Integer> byDay = values.getOrDefault(g.id(), Collections.emptyMap());

            // recorrer la semana
            int completedDays = 0;
            List<Boolean> daysCompleted = new ArrayList<>();
            for (LocalDate d = weekStart; !d.isAfter(weekEnd); d = d.plusDays(1)) {
                boolean completed = byDay.getOrDefault(d, 0) > 0; // al menos un 1
                daysCompleted.add(completed);
                if (completed) completedDays++;
            }

            int target = Optional.ofNullable(g.weeklyTarget()).orElse(0);
            int remaining = Math.max(0, target - completedDays);
            double percent = (target <= 0) ? 0.0 : (completedDays * 100.0 / target);

            // streaks dentro del rango
            int best = 0, run = 0;
            for (boolean c : daysCompleted) {
                if (c) { run++; best = Math.max(best, run); } else run = 0;
            }
            int cur = 0;
            for (int i = daysCompleted.size()-1; i >= 0; i--) {
                if (daysCompleted.get(i)) cur++; else break;
            }

            out.add(new Result(
                    g.id(), g.defaultId(), g.goalName(), target,
                    completedDays, remaining, percent, g.isActive(), cur, best
            ));
        }
        return out;
    }

    // DTO de aplicación
    public static final class Result {
        private final UUID goalId;
        private final Integer defaultId;
        private final String goalName;
        private final Integer weeklyTarget;
        private final Integer completedThisWeek;
        private final Integer remainingThisWeek;
        private final Double progressPercent;
        private final Boolean isActive;
        private final Integer streakCurrent;
        private final Integer streakBest;

        public Result(UUID goalId, Integer defaultId, String goalName, Integer weeklyTarget,
                      Integer completedThisWeek, Integer remainingThisWeek, Double progressPercent,
                      Boolean isActive, Integer streakCurrent, Integer streakBest) {
            this.goalId = goalId; this.defaultId = defaultId; this.goalName = goalName;
            this.weeklyTarget = weeklyTarget; this.completedThisWeek = completedThisWeek;
            this.remainingThisWeek = remainingThisWeek; this.progressPercent = progressPercent;
            this.isActive = isActive; this.streakCurrent = streakCurrent; this.streakBest = streakBest;
        }
        public UUID getGoalId() { return goalId; }
        public Integer getDefaultId() { return defaultId; }
        public String getGoalName() { return goalName; }
        public Integer getWeeklyTarget() { return weeklyTarget; }
        public Integer getCompletedThisWeek() { return completedThisWeek; }
        public Integer getRemainingThisWeek() { return remainingThisWeek; }
        public Double getProgressPercent() { return progressPercent; }
        public Boolean getIsActive() { return isActive; }
        public Integer getStreakCurrent() { return streakCurrent; }
        public Integer getStreakBest() { return streakBest; }
    }
}
