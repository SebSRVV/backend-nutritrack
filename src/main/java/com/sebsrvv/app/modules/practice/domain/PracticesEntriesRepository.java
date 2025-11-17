package com.sebsrvv.app.modules.practice.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PracticesEntriesRepository extends JpaRepository<PracticesEntries, UUID> {
    List<PracticesEntries> findByPracticeId(UUID practiceId);
}
