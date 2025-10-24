package com.sebsrvv.app.modules.reports.application;

import com.sebsrvv.app.modules.reports.domain.Report;
import com.sebsrvv.app.modules.reports.infra.ReportRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ReportService {

    private final ReportRepository repository;

    public ReportService(ReportRepository repository) {
        this.repository = repository;
    }

    public List<Report> getReportsByUser(UUID userId) {
        return repository.findByUserIdOrderByGeneratedAtDesc(userId);
    }

    public Report save(Report report) {
        return repository.save(report);
    }
}
