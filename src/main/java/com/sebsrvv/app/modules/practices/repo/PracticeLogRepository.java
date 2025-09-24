package com.sebsrvv.app.modules.practices.repo;

import com.sebsrvv.app.modules.practices.entity.PracticeLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.*;

public interface PracticeLogRepository extends JpaRepository<PracticeLog, java.util.UUID> {
    List<PracticeLog> findByUser_idAndPractice_idAndLogged_atBetween(UUID userId, UUID practiceId, OffsetDateTime from, OffsetDateTime to);
}
