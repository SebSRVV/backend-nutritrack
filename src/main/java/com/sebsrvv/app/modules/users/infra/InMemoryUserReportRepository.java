package com.sebsrvv.app.modules.users.infra;

import com.sebsrvv.app.modules.users.domain.UserReport;
import com.sebsrvv.app.modules.users.port.out.UserReportRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryUserReportRepository implements UserReportRepository {
    private final ConcurrentHashMap<UUID, UserReport> data = new ConcurrentHashMap<>();
    @Override public UserReport save(UserReport report) { data.put(report.getId(), report); return report; }
    @Override public Optional<UserReport> findById(UUID id) { return Optional.ofNullable(data.get(id)); }
}
