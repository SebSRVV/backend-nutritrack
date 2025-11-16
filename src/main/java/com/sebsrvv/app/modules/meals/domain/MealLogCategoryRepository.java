package com.sebsrvv.app.modules.meals.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MealLogCategoryRepository extends JpaRepository<MealLogCategory, Long> {
    Optional<MealLogCategory> findByMealLogId(Long mealLogId);
    void deleteByMealLogId(Long mealLogId);
}
