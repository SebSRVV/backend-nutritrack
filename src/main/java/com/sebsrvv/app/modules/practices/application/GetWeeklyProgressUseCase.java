// modules/practices/application/GetWeeklyProgressUseCase.java
package com.sebsrvv.app.modules.practices.application;

import com.sebsrvv.app.modules.practices.domain.port.PracticeProgressQueryPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class GetWeeklyProgressUseCase {

    private final PracticeProgressQueryPort port;

    public GetWeeklyProgressUseCase(PracticeProgressQueryPort port) {
        this.port = port;
    }

    public List<PracticeProgress> execute(UUID userId, LocalDate weekStart, LocalDate weekEnd) {
        if (weekStart == null || weekEnd == null || weekEnd.isBefore(weekStart)) {
            throw new IllegalArgumentException("Rango inválido (weekStart <= weekEnd requerido)");
        }

        var practices = port.findUserPractices(userId);
        if (practices.isEmpty()) return List.of();

        var practiceIds = practices.stream().map(PracticeProgressQueryPort.UserPractice::id).toList();
        var counts = port.findDailyCounts(userId, practiceIds, weekStart, weekEnd);

        List<PracticeProgress> out = new ArrayList<>();
        for (var p : practices) {
            Map<LocalDate, Integer> byDay = counts.getOrDefault(p.id(), Collections.emptyMap());

            // construir días del rango
            List<PracticeProgress.Day> days = new ArrayList<>();
            LocalDate d = weekStart;
            int completedDays = 0;
            while (!d.isAfter(weekEnd)) {
                int c = byDay.getOrDefault(d, 0);
                boolean completed = c > 0;
                if (completed) completedDays++;
                days.add(new PracticeProgress.Day(d, completed, c));
                d = d.plusDays(1);
            }

            int target = Optional.ofNullable(p.frequencyTarget()).orElse(0);
            int remaining = Math.max(0, target - completedDays);
            double percent = (target <= 0) ? 0.0 : (completedDays * 100.0 / target);

            // streaks (dentro del rango)
            int streakBest = 0, streakCur = 0;
            int run = 0;
            for (var day : days) {
                if (day.completed()) {
                    run++;
                } else {
                    streakBest = Math.max(streakBest, run);
                    run = 0;
                }
            }
            streakBest = Math.max(streakBest, run);
            streakCur = 0;
            for (int i = days.size() - 1; i >= 0; i--) {
                if (days.get(i).completed()) streakCur++;
                else break;
            }

            out.add(new PracticeProgress(
                    p.id(), p.defaultId(), p.practiceName(), target,
                    completedDays, remaining, percent, p.isActive(), days, streakCur, streakBest
            ));
        }
        return out;
    }

    // -------- DTO de aplicación (dominio de salida) --------
    public static final class PracticeProgress {
        private final UUID practiceId;
        private final Integer defaultId;
        private final String practiceName;
        private final Integer frequencyTarget;
        private final Integer completionsThisWeek;
        private final Integer remainingThisWeek;
        private final Double  progressPercent;
        private final Boolean isActive;
        private final List<Day> days;
        private final Integer streakCurrent;
        private final Integer streakBest;

        public PracticeProgress(UUID practiceId, Integer defaultId, String practiceName,
                                Integer frequencyTarget, Integer completionsThisWeek,
                                Integer remainingThisWeek, Double progressPercent, Boolean isActive,
                                List<Day> days, Integer streakCurrent, Integer streakBest) {
            this.practiceId = practiceId;
            this.defaultId = defaultId;
            this.practiceName = practiceName;
            this.frequencyTarget = frequencyTarget;
            this.completionsThisWeek = completionsThisWeek;
            this.remainingThisWeek = remainingThisWeek;
            this.progressPercent = progressPercent;
            this.isActive = isActive;
            this.days = days;
            this.streakCurrent = streakCurrent;
            this.streakBest = streakBest;
        }

        public UUID getPracticeId() { return practiceId; }
        public Integer getDefaultId() { return defaultId; }
        public String getPracticeName() { return practiceName; }
        public Integer getFrequencyTarget() { return frequencyTarget; }
        public Integer getCompletionsThisWeek() { return completionsThisWeek; }
        public Integer getRemainingThisWeek() { return remainingThisWeek; }
        public Double getProgressPercent() { return progressPercent; }
        public Boolean getIsActive() { return isActive; }
        public List<Day> getDays() { return days; }
        public Integer getStreakCurrent() { return streakCurrent; }
        public Integer getStreakBest() { return streakBest; }

        public record Day(LocalDate date, boolean completed, int count) {}
    }
}
