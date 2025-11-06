package com.sebsrvv.app.modules.meals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * Repositorio JPA para manejar las operaciones con la tabla meals.
 */
public interface MealRepository extends JpaRepository<Meal, UUID> {

    /**
     * Obtiene las comidas de un usuario en una fecha específica.
     */
    List<Meal> findByUserIdAndLoggedAt(UUID userId, LocalDate loggedAt);

    /**
     * Obtiene las comidas de un usuario dentro de un rango de fechas.
     */
    @Query("SELECT m FROM Meal m WHERE m.userId = :userId AND m.loggedAt BETWEEN :from AND :to ORDER BY m.loggedAt ASC")
    List<Meal> findMealsBetweenDates(@Param("userId") UUID userId,
                                     @Param("from") LocalDate from,
                                     @Param("to") LocalDate to);
}
