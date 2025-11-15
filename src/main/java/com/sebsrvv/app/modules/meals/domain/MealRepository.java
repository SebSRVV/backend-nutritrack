package com.sebsrvv.app.modules.meals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<MealLog, Long> { // Long, no UUID

    List<MealLog> findByUserId(Long userId); // Long

    List<MealLog> findByUserIdAndLoggedAtBetweenOrderByLoggedAtAsc(Long userId, Instant start, Instant end);

    List<MealLog> findByUserIdAndLoggedAt(Long userId, Instant loggedAt);
}
