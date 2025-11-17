package com.sebsrvv.app.modules.practice.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PracticesRepository extends JpaRepository<Practices, UUID> {
    List<Practices> findByUserId(UUID userId);
    //List<Practices> findByPracticeId(UUID PracticeId);
}
