package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.FoodCategoryRepository;
import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.modules.meals.exception.FoodCategoryNotFoundException;
import com.sebsrvv.app.modules.meals.exception.InvalidMealException;
import com.sebsrvv.app.modules.meals.exception.MealNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class MealService {

    private final MealRepository mealRepository;
    private final FoodCategoryRepository foodCategoryRepository;

    public MealService(MealRepository mealRepository,
                       FoodCategoryRepository foodCategoryRepository) {
        this.mealRepository = mealRepository;
        this.foodCategoryRepository = foodCategoryRepository;
    }

    // -------- Commands internos para desacoplar de la web layer --------

    public record CreateMealCommand(
            UUID userId,
            String description,
            int calories,
            BigDecimal proteinGrams,
            BigDecimal carbsGrams,
            BigDecimal fatGrams,
            String mealType,
            OffsetDateTime loggedAt,
            Set<Integer> categoryIds
    ) {}

    public record UpdateMealCommand(
            UUID mealId,
            UUID userId,
            String description,
            Integer calories,
            BigDecimal proteinGrams,
            BigDecimal carbsGrams,
            BigDecimal fatGrams,
            String mealType,
            OffsetDateTime loggedAt,
            Set<Integer> categoryIds
    ) {}

    // ----------------- Casos de uso -----------------

    public Meal createMeal(CreateMealCommand command) {
        validateCategories(command.categoryIds());

        MealType mealType = parseMealType(command.mealType());

        Meal meal = Meal.create(
                command.userId(),
                command.description(),
                command.calories(),
                command.proteinGrams(),
                command.carbsGrams(),
                command.fatGrams(),
                mealType,
                command.loggedAt(),
                command.categoryIds()
        );

        return mealRepository.save(meal);
    }

    public Meal updateMeal(UpdateMealCommand command) {
        Meal existing = mealRepository.findByIdAndUserId(command.mealId(), command.userId())
                .orElseThrow(() -> new MealNotFoundException(command.mealId()));

        if (command.categoryIds() != null) {
            validateCategories(command.categoryIds());
        }

        MealType mealType = command.mealType() != null
                ? parseMealType(command.mealType())
                : null;

        existing.update(
                command.description(),
                command.calories(),
                command.proteinGrams(),
                command.carbsGrams(),
                command.fatGrams(),
                mealType,
                command.loggedAt(),
                command.categoryIds()
        );

        return mealRepository.save(existing);
    }

    public void deleteMeal(UUID mealId, UUID userId) {
        // para asegurar que es suyo
        Meal existing = mealRepository.findByIdAndUserId(mealId, userId)
                .orElseThrow(() -> new MealNotFoundException(mealId));

        mealRepository.deleteByIdAndUserId(existing.getId(), userId);
    }

    @Transactional(readOnly = true)
    public Meal getMeal(UUID mealId, UUID userId) {
        return mealRepository.findByIdAndUserId(mealId, userId)
                .orElseThrow(() -> new MealNotFoundException(mealId));
    }

    @Transactional(readOnly = true)
    public List<Meal> getMeals(UUID userId, LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new InvalidMealException("from y to son obligatorios");
        }
        return mealRepository.findByUserIdAndDateRange(userId, from, to);
    }

    // ----------------- helpers privados -----------------

    private MealType parseMealType(String raw) {
        try {
            return MealType.valueOf(raw.toUpperCase());
        } catch (Exception ex) {
            throw new InvalidMealException("mealType inválido: " + raw);
        }
    }

    private void validateCategories(Set<Integer> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) return;

        var found = foodCategoryRepository.findAllById(categoryIds);
        if (found.size() != categoryIds.size()) {
            throw new FoodCategoryNotFoundException();
        }
    }
}
