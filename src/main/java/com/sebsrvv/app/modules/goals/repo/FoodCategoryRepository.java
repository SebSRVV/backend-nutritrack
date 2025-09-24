package com.sebsrvv.app.modules.goals.repo;

import com.sebsrvv.app.modules.goals.entity.FoodCategory;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FoodCategoryRepository extends JpaRepository<FoodCategory, Integer> {}
