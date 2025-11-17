package com.sebsrvv.app.modules.meals.domain;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface MealRepository {

    Meal save(Meal meal);

    Optional<Meal> findByIdAndUserId(UUID id, UUID userId);

    List<Meal> findByUserIdAndDateRange(UUID userId, LocalDate from, LocalDate to);

    void deleteByIdAndUserId(UUID id, UUID userId);
}