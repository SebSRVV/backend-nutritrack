package com.sebsrvv.app.modules.meals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MealRepository extends JpaRepository<MealLog, Long> {

    // Usadas por tu servicio; las dejo tal cual
    List<MealLog> findByUserId(String userId);

    List<MealLog> findByUserIdAndLoggedAtBetweenOrderByLoggedAtAsc(String userId, Instant start, Instant end);

    List<MealLog> findByUserIdAndLoggedAt(String userId, Instant loggedAt);
}
