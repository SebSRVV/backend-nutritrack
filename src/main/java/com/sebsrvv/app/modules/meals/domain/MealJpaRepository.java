package com.sebsrvv.app.modules.meals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;

@Repository
public interface MealJpaRepository extends JpaRepository<MealLog, Long> {

    List<MealLog> findByUserIdAndLoggedAtBetweenOrderByLoggedAtAsc(String userId, Instant from, Instant to);
}
