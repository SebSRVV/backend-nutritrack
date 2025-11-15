package com.sebsrvv.app.modules.meals.infra;

import com.sebsrvv.app.modules.meals.domain.MealLog;
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

    public List<MealLog> findMealsBetweenDates(UUID userId, LocalDate from, LocalDate to) {
        return entityManager.createQuery(
                        "SELECT m FROM MealLog m WHERE m.userId = :userId AND m.loggedAt BETWEEN :from AND :to", MealLog.class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}
