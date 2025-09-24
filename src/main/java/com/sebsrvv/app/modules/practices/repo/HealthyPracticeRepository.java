package com.sebsrvv.app.modules.practices.repo;

import com.sebsrvv.app.modules.practices.entity.HealthyPractice;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;

public interface HealthyPracticeRepository extends JpaRepository<HealthyPractice, java.util.UUID> {
    List<HealthyPractice> findByUser_id(UUID userId);
}
