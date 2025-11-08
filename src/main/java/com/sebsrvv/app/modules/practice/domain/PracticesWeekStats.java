package com.sebsrvv.app.modules.practice.domain;

import jakarta.persistence.*;
import lombok.Builder;
import org.hibernate.annotations.GenericGenerator;
import java.util.UUID;
import java.time.LocalDate;

@Entity
@Table(name="practice_weekly_stats_v2")
public class PracticesWeekStats {
    @Id
    @GeneratedValue(generator = "UUID")
    @GenericGenerator(name = "UUID", strategy = "org.hibernate.id.UUIDGenerator")
    @Column(name = "id", nullable = false, updatable = false, unique = true)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "practice_id", nullable = false)
    private UUID practiceId;

    @Column(name = "name")
    private String name;

    @Column(name = "days_per_week")
    private Short daysPerWeek;

    @Column(name = "achieved_days_last7")
    private Long achievedDaysLast7;

    @Column(name = "logged_days_last7")
    private Long loggedDaysLast7;

    @Column(name = "first_log_in_range")
    private LocalDate firstLogInRange;

    @Column(name = "last_log_in_range")
    private LocalDate lastLogInRange;

    // Getters y Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public UUID getPracticeId() {
        return practiceId;
    }

    public void setPracticeId(UUID practiceId) {
        this.practiceId = practiceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Short getDaysPerWeek() {
        return daysPerWeek;
    }

    public void setDaysPerWeek(Short daysPerWeek) {
        this.daysPerWeek = daysPerWeek;
    }

    public Long getAchievedDaysLast7() {
        return achievedDaysLast7;
    }

    public void setAchievedDaysLast7(Long achievedDaysLast7) {
        this.achievedDaysLast7 = achievedDaysLast7;
    }

    public Long getLoggedDaysLast7() {
        return loggedDaysLast7;
    }

    public void setLoggedDaysLast7(Long loggedDaysLast7) {
        this.loggedDaysLast7 = loggedDaysLast7;
    }

    public LocalDate getFirstLogInRange() {
        return firstLogInRange;
    }

    public void setFirstLogInRange(LocalDate firstLogInRange) {
        this.firstLogInRange = firstLogInRange;
    }

    public LocalDate getLastLogInRange() {
        return lastLogInRange;
    }

    public void setLastLogInRange(LocalDate lastLogInRange) {
        this.lastLogInRange = lastLogInRange;
    }
}
