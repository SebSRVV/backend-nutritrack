package com.sebsrvv.app.modules.meals.infra;

import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodCategoryJpaRepository extends JpaRepository<FoodCategoryEntity, Integer> {
}
