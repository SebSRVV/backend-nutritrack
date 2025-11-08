package com.sebsrvv.app.modules.reports.infra;

import com.sebsrvv.app.modules.reports.domain.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ReportRepository extends JpaRepository<Report, UUID> {
    List<Report> findByUserIdOrderByGeneratedAtDesc(UUID userId);
}
