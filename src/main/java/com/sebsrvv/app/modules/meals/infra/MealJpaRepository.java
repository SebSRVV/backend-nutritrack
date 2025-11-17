package com.sebsrvv.app.modules.meals.infra;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MealJpaRepository extends JpaRepository<MealEntity, UUID> {

    Optional<MealEntity> findByIdAndUserId(UUID id, UUID userId);

    List<MealEntity> findByUserIdAndLoggedAtBetween(
            UUID userId,
            OffsetDateTime from,
            OffsetDateTime to
    );

    void deleteByIdAndUserId(UUID id, UUID userId);
}
