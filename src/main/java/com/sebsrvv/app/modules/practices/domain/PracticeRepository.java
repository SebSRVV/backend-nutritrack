package com.sebsrvv.app.modules.practices.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PracticeRepository extends JpaRepository<Practice, UUID> {
}
