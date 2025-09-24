package com.sebsrvv.app.modules.meals.repo;

import com.sebsrvv.app.modules.meals.entity.MealLog;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.OffsetDateTime;
import java.util.*;

public interface MealLogRepository extends JpaRepository<MealLog, java.util.UUID> {
    List<MealLog> findByUser_idAndLogged_atBetween(java.util.UUID userId, OffsetDateTime from, OffsetDateTime to);
}
