package com.sebsrvv.app.modules.meals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Repository
public interface MealJpaRepository extends JpaRepository<Meal, UUID> {

    // Buscar comidas de un usuario en un instante concreto (exacto)
    List<Meal> findByUserIdAndLoggedAt(UUID userId, Instant loggedAt);

    // Buscar comidas entre dos instantes (rango)
    List<Meal> findByUserIdAndLoggedAtBetweenOrderByLoggedAtAsc(UUID userId, Instant from, Instant to);

    // Alternativa para usar LocalDate en queries (si prefieres normalizar a 00:00 - 23:59 desde servicio)
    // List<Meal> findByUserIdAndLoggedAtBetweenOrderByLoggedAtAsc(UUID userId, Instant fromStartOfDay, Instant toEndOfDay);
}
