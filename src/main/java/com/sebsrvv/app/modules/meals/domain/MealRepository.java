package com.sebsrvv.app.modules.meals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MealRepository extends JpaRepository<Meal, UUID> {

    List<Meal> findByUserId(UUID userId);

    List<Meal> findByUserIdAndLoggedAtBetween(UUID userId, Instant start, Instant end);

    @Query("SELECT m FROM Meal m WHERE m.userId = :userId AND m.loggedAt >= :start AND m.loggedAt < :end")
    List<Meal> findByUserIdAndLoggedAtBetweenDates(@Param("userId") UUID userId,
                                                   @Param("start") Instant start,
                                                   @Param("end") Instant end);
}
