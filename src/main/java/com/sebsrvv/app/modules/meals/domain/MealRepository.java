// src/main/java/com/sebsrvv/app/modules/meals/domain/MealRepository.java
package com.sebsrvv.app.modules.meals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA unificado: contiene las consultas que usaremos.
 */
@Repository
public interface MealRepository extends JpaRepository<Meal, UUID> {

    List<Meal> findByUserId(UUID userId);

    List<Meal> findByUserIdAndLoggedAtBetweenOrderByLoggedAtAsc(UUID userId, Instant start, Instant end);

    List<Meal> findByUserIdAndLoggedAt(UUID userId, Instant loggedAt);
}
