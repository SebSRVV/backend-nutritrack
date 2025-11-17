package com.sebsrvv.app.modules.meals.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MealRepository extends JpaRepository<Meal, UUID> {

    Optional<Meal> findByIdAndUserId(UUID id, UUID userId);

    List<Meal> findByUserIdAndLoggedAtBetween(UUID userId, OffsetDateTime from, OffsetDateTime to);

    void deleteByIdAndUserId(UUID id, UUID userId);
}
