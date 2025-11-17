package com.sebsrvv.app.modules.meals.infra;

import com.sebsrvv.app.modules.meals.domain.FoodCategory;
import com.sebsrvv.app.modules.meals.domain.FoodCategoryRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Repository
public class FoodCategoryRepositoryImpl implements FoodCategoryRepository {

    private final FoodCategoryJpaRepository jpaRepository;

    public FoodCategoryRepositoryImpl(FoodCategoryJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public List<FoodCategory> findAllById(Set<Integer> ids) {
        return jpaRepository.findAllById(ids).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private FoodCategory toDomain(FoodCategoryEntity e) {
        return new FoodCategory(
                e.getId(),
                e.getName(),
                e.getDescription()
        );
    }
}
