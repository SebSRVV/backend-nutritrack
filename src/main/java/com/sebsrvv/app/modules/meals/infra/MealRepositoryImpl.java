package com.sebsrvv.app.modules.meals.infra;

import com.sebsrvv.app.modules.meals.domain.MealLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public class MealRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    // Busca por userId (Long) y rango Instant (loggedAt)
    public List<MealLog> findMealsBetweenInstants(Long userId, Instant from, Instant to) {
        return entityManager.createQuery(
                        "SELECT m FROM MealLog m WHERE m.userId = :userId AND m.loggedAt BETWEEN :from AND :to ORDER BY m.loggedAt ASC",
                        MealLog.class)
                .setParameter("userId", userId)
                .setParameter("from", from)
                .setParameter("to", to)
                .getResultList();
    }
}
