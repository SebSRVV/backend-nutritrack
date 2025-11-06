package com.sebsrvv.app.modules.meals.infra;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Repository
public class MealRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    public List<Meal> findMealsBetweenDates(UUID userId, LocalDate from, LocalDate to) {
        return entityManager.createQuery(
                        "SELECT m FROM Meal m WHERE m.userId = :userId AND m.loggedAt BETWEEN :from AND :to", Meal.class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}
