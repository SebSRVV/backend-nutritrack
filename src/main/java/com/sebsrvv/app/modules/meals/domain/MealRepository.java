package com.sebsrvv.app.modules.meals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public interface MealRepository extends JpaRepository<Meal, UUID> {

    // Obtener comidas de un usuario en una fecha específica
    List<Meal> findByUserIdAndLoggedAt(UUID userId, LocalDate loggedAt);

    // Obtener comidas entre dos fechas para un usuario
    @Query("SELECT m FROM Meal m WHERE m.userId = :userId AND m.loggedAt BETWEEN :from AND :to ORDER BY m.loggedAt ASC")
    List<Meal> findMealsBetweenDates(@Param("userId") UUID userId,
                                     @Param("from") LocalDate from,
                                     @Param("to") LocalDate to);
}
