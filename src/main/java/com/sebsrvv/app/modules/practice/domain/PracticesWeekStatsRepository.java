package com.sebsrvv.app.modules.practice.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PracticesWeekStatsRepository extends JpaRepository<PracticesWeekStats, UUID> {
    List<PracticesWeekStats> findByPracticeId(UUID practiceId);
}
