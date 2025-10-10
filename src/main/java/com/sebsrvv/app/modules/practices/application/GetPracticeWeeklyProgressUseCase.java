// modules/practices/application/GetPracticeWeeklyProgressUseCase.java
package com.sebsrvv.app.modules.practices.application;

import com.sebsrvv.app.modules.practices.domain.port.PracticeProgressDetailPort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class GetPracticeWeeklyProgressUseCase {

    private final PracticeProgressDetailPort port;

    public GetPracticeWeeklyProgressUseCase(PracticeProgressDetailPort port) {
        this.port = port;
    }

    public Result execute(UUID userId, UUID practiceId, LocalDate weekStart) {
        if (weekStart == null) throw new IllegalArgumentException("weekStart requerido");
        LocalDate weekEnd = weekStart.plusDays(6);

        var practice = port.findPractice(userId, practiceId);
        if (practice == null) throw new NoSuchElementException("Práctica no encontrada para el usuario");

        var logs = port.findLogs(userId, practiceId, weekStart, weekEnd);

        // Agrupar logs por día del rango
        Map<LocalDate, List<PracticeProgressDetailPort.Log>> byDay = logs.stream()
                .collect(Collectors.groupingBy(PracticeProgressDetailPort.Log::loggedDate));

        // Construir días del rango
        List<Result.Day> days = new ArrayList<>();
        LocalDate d = weekStart;
        int completedDays = 0;
        while (!d.isAfter(weekEnd)) {
            var dayLogs = byDay.getOrDefault(d, List.of());
            boolean completed = !dayLogs.isEmpty();
            if (completed) completedDays++;
            days.add(new Result.Day(
                    d,
                    completed,
                    dayLogs.stream()
                            .map(l -> new Result.Log(l.id().toString(), l.loggedAt().toString()))
                            .toList()
            ));
            d = d.plusDays(1);
        }

        int target = Optional.ofNullable(practice.frequencyTarget()).orElse(0);
        double percent = (target <= 0) ? 0.0 : (completedDays * 100.0 / target);

        return new Result(
                practice.id(),
                practice.practiceName(),
                target,
                completedDays,
                days,
                percent
        );
    }

    // ---------- DTO de aplicación ----------
    public static final class Result {
        private final UUID practiceId;
        private final String practiceName;
        private final Integer frequencyTarget;
        private final Integer completionsThisWeek;
        private final List<Day> days;
        private final Double progressPercent;

        public Result(UUID practiceId, String practiceName, Integer frequencyTarget,
                      Integer completionsThisWeek, List<Day> days, Double progressPercent) {
            this.practiceId = practiceId;
            this.practiceName = practiceName;
            this.frequencyTarget = frequencyTarget;
            this.completionsThisWeek = completionsThisWeek;
            this.days = days;
            this.progressPercent = progressPercent;
        }

        public UUID getPracticeId() { return practiceId; }
        public String getPracticeName() { return practiceName; }
        public Integer getFrequencyTarget() { return frequencyTarget; }
        public Integer getCompletionsThisWeek() { return completionsThisWeek; }
        public List<Day> getDays() { return days; }
        public Double getProgressPercent() { return progressPercent; }

        public record Day(LocalDate date, boolean completed, List<Log> logs) {}
        public record Log(String id, String loggedAt) {}
    }
}
