package com.sebsrvv.app.modules.meals.domain;

import java.util.List;
import java.util.Set;

public interface FoodCategoryRepository {

    List<FoodCategory> findAllById(Set<Integer> ids);
}
