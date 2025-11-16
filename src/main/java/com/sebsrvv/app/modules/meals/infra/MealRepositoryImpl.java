package com.sebsrvv.app.modules.meals.infra;

import com.sebsrvv.app.modules.meals.domain.MealLog;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

@Repository
public class MealRepositoryImpl {

    @PersistenceContext
    private EntityManager entityManager;

    /**
     * Busca meals de un usuario entre dos instants (inclusive),
     * ordenadas por loggedAt asc.
     */
    public List<MealLog> findMealsBetweenInstants(String userId, Instant from, Instant to) {
        if (userId == null || from == null || to == null) {
            return Collections.emptyList();
        }
        if (from.isAfter(to)) {
            Instant tmp = from;
            from = to;
            to = tmp;
        }

        TypedQuery<MealLog> q = entityManager.createQuery(
                "SELECT m FROM MealLog m WHERE m.userId = :userId AND m.loggedAt BETWEEN :from AND :to ORDER BY m.loggedAt ASC",
                MealLog.class);
        q.setParameter("userId", userId);
        q.setParameter("from", from);
        q.setParameter("to", to);
        return q.getResultList();
    }
}
