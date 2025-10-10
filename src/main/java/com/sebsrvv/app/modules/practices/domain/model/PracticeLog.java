// domain/model/PracticeLog.java
package com.sebsrvv.app.modules.practices.domain.model;

import java.time.OffsetDateTime;
import java.time.LocalDate;
import java.util.UUID;

public class PracticeLog {
    private UUID id;
    private UUID userId;
    private UUID practiceId;
    private OffsetDateTime loggedAt;
    private LocalDate loggedDate;
    private String note;
    private OffsetDateTime createdAt;

    public PracticeLog() {}

    public PracticeLog(UUID id, UUID userId, UUID practiceId,
                       OffsetDateTime loggedAt, LocalDate loggedDate,
                       String note, OffsetDateTime createdAt) {
        this.id = id; this.userId = userId; this.practiceId = practiceId;
        this.loggedAt = loggedAt; this.loggedDate = loggedDate; this.note = note; this.createdAt = createdAt;
    }
    // getters
    public UUID getId() { return id; }
    public UUID getUserId() { return userId; }
    public UUID getPracticeId() { return practiceId; }
    public OffsetDateTime getLoggedAt() { return loggedAt; }
    public LocalDate getLoggedDate() { return loggedDate; }
    public String getNote() { return note; }
    public OffsetDateTime getCreatedAt() { return createdAt; }
    // setters
    public void setId(UUID id) { this.id = id; }
    public void setUserId(UUID userId) { this.userId = userId; }
    public void setPracticeId(UUID practiceId) { this.practiceId = practiceId; }
    public void setLoggedAt(OffsetDateTime loggedAt) { this.loggedAt = loggedAt; }
    public void setLoggedDate(LocalDate loggedDate) { this.loggedDate = loggedDate; }
    public void setNote(String note) { this.note = note; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
}
