package com.sebsrvv.app.modules.meals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MealLogCategoryRepository extends JpaRepository<MealLogCategory, Long> {
    // consultas adicionales si hicieras falta
}
