package com.sebsrvv.app.modules.users.port.out;

import com.sebsrvv.app.modules.users.domain.UserReport;

import java.util.Optional;
import java.util.UUID;

public interface UserReportRepository {
    UserReport save(UserReport report);
    Optional<UserReport> findById(UUID id);
}
