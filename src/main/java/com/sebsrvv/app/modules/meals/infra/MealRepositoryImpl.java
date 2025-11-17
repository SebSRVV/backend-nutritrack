package com.sebsrvv.app.modules.meals.infra;

import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.domain.MealType;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Repository
public class MealRepositoryImpl implements MealRepository {

    private final MealJpaRepository mealJpaRepository;

    public MealRepositoryImpl(MealJpaRepository mealJpaRepository) {
        this.mealJpaRepository = mealJpaRepository;
    }

    @Override
    public Meal save(Meal meal) {
        MealEntity entity = toEntity(meal);
        MealEntity saved = mealJpaRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Meal> findByIdAndUserId(UUID id, UUID userId) {
        return mealJpaRepository.findByIdAndUserId(id, userId)
                .map(this::toDomain);
    }

    @Override
    public List<Meal> findByUserIdAndDateRange(UUID userId, LocalDate from, LocalDate to) {
        OffsetDateTime fromDateTime = from.atStartOfDay().atOffset(OffsetDateTime.now().getOffset());
        OffsetDateTime toDateTime = to.atTime(LocalTime.MAX).atOffset(OffsetDateTime.now().getOffset());

        return mealJpaRepository.findByUserIdAndLoggedAtBetween(userId, fromDateTime, toDateTime)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void deleteByIdAndUserId(UUID id, UUID userId) {
        mealJpaRepository.deleteByIdAndUserId(id, userId);
    }

    // ---------------- mapeos domain <-> entity ------------------

    private MealEntity toEntity(Meal meal) {
        MealEntity e = new MealEntity();
        e.setId(meal.getId());
        e.setUserId(meal.getUserId());
        e.setDescription(meal.getDescription());
        e.setCalories(meal.getCalories());
        e.setProteinGrams(meal.getProteinGrams());
        e.setCarbsGrams(meal.getCarbsGrams());
        e.setFatGrams(meal.getFatGrams());
        e.setMealType(meal.getMealType().name());
        e.setLoggedAt(meal.getLoggedAt());
        e.setCreatedAt(meal.getCreatedAt());

        // categorías: como el agregado solo guarda ids, aquí podrías
        // resolverlas si quieres tenerlas completamente cargadas.
        // Para algo simple, solo dejamos vacío y rely en otro flow,
        // salvo que quieras inyectar FoodCategoryJpaRepository aquí.
        // e.setCategories(...);

        return e;
    }

    private Meal toDomain(MealEntity e) {
        // convertir categorías a Set<Integer>
        var categoryIds = e.getCategories().stream()
                .map(FoodCategoryEntity::getId)
                .collect(Collectors.toSet());

        return new Meal(
                e.getId(),
                e.getUserId(),
                e.getDescription(),
                e.getCalories(),
                e.getProteinGrams(),
                e.getCarbsGrams(),
                e.getFatGrams(),
                MealType.valueOf(e.getMealType().toUpperCase()),
                e.getLoggedAt(),
                e.getCreatedAt(),
                categoryIds
        );
    }
}
