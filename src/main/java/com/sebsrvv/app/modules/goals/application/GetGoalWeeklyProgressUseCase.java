// modules/goals/application/GetGoalWeeklyProgressUseCase.java
package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.domain.port.GoalProgressDetailPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GetGoalWeeklyProgressUseCase {

    private final GoalProgressDetailPort port;

    public GetGoalWeeklyProgressUseCase(GoalProgressDetailPort port) {
        this.port = port;
    }

    public Result execute(UUID userId, UUID goalId, LocalDate weekStart, LocalDate weekEnd) {
        if (weekStart == null || weekEnd == null || weekEnd.isBefore(weekStart))
            throw new IllegalArgumentException("Rango inválido (weekStart <= weekEnd)");

        var goal = port.findGoal(userId, goalId);
        if (goal == null) throw new NoSuchElementException("Goal no encontrado para el usuario");

        // Traemos todos los registros del rango
        var logs = port.findDaily(userId, goalId, weekStart, weekEnd);

        // Agrupar por fecha (si hay múltiples filas, usamos max(value) y la última nota no nula)
        Map<LocalDate, Integer> valueByDate = new HashMap<>();
        Map<LocalDate, String>  noteByDate  = new HashMap<>();

        for (var l : logs) {
            valueByDate.merge(l.date(), l.value() == null ? 0 : l.value(), Math::max);
            if (l.note() != null && !l.note().isBlank()) noteByDate.put(l.date(), l.note());
        }

        // Construimos respuesta por día
        List<Result.Day> days = new ArrayList<>();
        int completedDays = 0;
        for (LocalDate d = weekStart; !d.isAfter(weekEnd); d = d.plusDays(1)) {
            int v = valueByDate.getOrDefault(d, 0);
            String note = noteByDate.getOrDefault(d, null);
            days.add(new Result.Day(d, v, note));
            if (v > 0) completedDays++;
        }

        int target = Optional.ofNullable(goal.weeklyTarget()).orElse(0);
        int remaining = Math.max(0, target - completedDays);
        double percent = (target <= 0) ? 0.0 : (completedDays * 100.0 / target);

        // streaks (dentro del rango)
        int best = 0, run = 0;
        for (var day : days) {
            if (day.value() > 0) { run++; best = Math.max(best, run); } else run = 0;
        }
        int current = 0;
        for (int i = days.size() - 1; i >= 0; i--) {
            if (days.get(i).value() > 0) current++; else break;
        }

        return new Result(
                goal.id(), goal.defaultId(), goal.goalName(),
                target, days, completedDays, remaining, percent,
                goal.isActive(), current, best
        );
    }

    // ---------- DTO de aplicación ----------
    public static final class Result {
        private final UUID goalId;
        private final Integer defaultId;
        private final String goalName;
        private final Integer weeklyTarget;
        private final List<Day> days;
        private final Integer completedThisWeek;
        private final Integer remainingThisWeek;
        private final Double progressPercent;
        private final Boolean isActive;
        private final Integer streakCurrent;
        private final Integer streakBest;

        public Result(UUID goalId, Integer defaultId, String goalName, Integer weeklyTarget,
                      List<Day> days, Integer completedThisWeek, Integer remainingThisWeek,
                      Double progressPercent, Boolean isActive, Integer streakCurrent, Integer streakBest) {
            this.goalId = goalId; this.defaultId = defaultId; this.goalName = goalName; this.weeklyTarget = weeklyTarget;
            this.days = days; this.completedThisWeek = completedThisWeek; this.remainingThisWeek = remainingThisWeek;
            this.progressPercent = progressPercent; this.isActive = isActive;
            this.streakCurrent = streakCurrent; this.streakBest = streakBest;
        }

        public UUID getGoalId() { return goalId; }
        public Integer getDefaultId() { return defaultId; }
        public String getGoalName() { return goalName; }
        public Integer getWeeklyTarget() { return weeklyTarget; }
        public List<Day> getDays() { return days; }
        public Integer getCompletedThisWeek() { return completedThisWeek; }
        public Integer getRemainingThisWeek() { return remainingThisWeek; }
        public Double getProgressPercent() { return progressPercent; }
        public Boolean getIsActive() { return isActive; }
        public Integer getStreakCurrent() { return streakCurrent; }
        public Integer getStreakBest() { return streakBest; }

        public record Day(LocalDate date, int value, String note) {}
    }
}
