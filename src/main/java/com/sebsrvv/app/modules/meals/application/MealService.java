package com.sebsrvv.app.modules.meals.application;

import com.sebsrvv.app.modules.meals.domain.FoodCategory;
import com.sebsrvv.app.modules.meals.domain.FoodCategoryRepository;
import com.sebsrvv.app.modules.meals.domain.Meal;
import com.sebsrvv.app.modules.meals.domain.MealRepository;
import com.sebsrvv.app.modules.meals.domain.MealType;
import com.sebsrvv.app.modules.meals.exception.FoodCategoryNotFoundException;
import com.sebsrvv.app.modules.meals.exception.InvalidMealException;
import com.sebsrvv.app.modules.meals.exception.MealNotFoundException;
import com.sebsrvv.app.modules.meals.web.dto.CreateMealRequest;
import com.sebsrvv.app.modules.meals.web.dto.MealResponse;
import com.sebsrvv.app.modules.meals.web.dto.UpdateMealRequest;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class MealService {

    private final MealRepository meals;
    private final FoodCategoryRepository categories;

    public MealService(MealRepository meals, FoodCategoryRepository categories) {
        this.meals = meals;
        this.categories = categories;
    }

    @Transactional
    public MealResponse create(Jwt jwt, CreateMealRequest r) {
        UUID userId = UUID.fromString(jwt.getSubject());

        if (r.description() == null || r.description().isBlank()) {
            throw new InvalidMealException("description es obligatorio");
        }
        if (r.calories() == null || r.calories() < 0) {
            throw new InvalidMealException("calories debe ser >= 0");
        }

        MealType mealType = parseMealTypeOrDefault(r.mealType(), MealType.breakfast);

        Set<FoodCategory> categoryEntities = resolveCategories(r.categoryIds());

        Meal m = new Meal();
        m.setId(UUID.randomUUID());
        m.setUserId(userId);
        m.setDescription(r.description().trim());
        m.setCalories(r.calories());
        m.setProteinGrams(r.proteinGrams());
        m.setCarbsGrams(r.carbsGrams());
        m.setFatGrams(r.fatGrams());
        m.setMealType(mealType);
        m.setLoggedAt(r.loggedAt() != null ? r.loggedAt() : OffsetDateTime.now(ZoneOffset.UTC));
        m.setCreatedAt(OffsetDateTime.now(ZoneOffset.UTC));
        m.setCategories(new HashSet<>(categoryEntities));

        m = meals.save(m);
        return toResponse(m);
    }

    @Transactional
    public MealResponse update(Jwt jwt, UUID mealId, UpdateMealRequest r) {
        UUID userId = UUID.fromString(jwt.getSubject());

        Meal m = meals.findByIdAndUserId(mealId, userId)
                .orElseThrow(() -> new MealNotFoundException(mealId));

        if (r.description() != null && !r.description().isBlank()) {
            m.setDescription(r.description().trim());
        }
        if (r.calories() != null) {
            if (r.calories() < 0) {
                throw new InvalidMealException("calories debe ser >= 0");
            }
            m.setCalories(r.calories());
        }
        if (r.proteinGrams() != null) m.setProteinGrams(r.proteinGrams());
        if (r.carbsGrams() != null) m.setCarbsGrams(r.carbsGrams());
        if (r.fatGrams() != null) m.setFatGrams(r.fatGrams());

        if (r.mealType() != null) {
            m.setMealType(parseMealType(r.mealType()));
        }

        if (r.loggedAt() != null) {
            m.setLoggedAt(r.loggedAt());
        }

        if (r.categoryIds() != null) {
            Set<FoodCategory> cats = resolveCategories(r.categoryIds());
            m.setCategories(new HashSet<>(cats));
        }

        m = meals.save(m);
        return toResponse(m);
    }

    @Transactional
    public void delete(Jwt jwt, UUID mealId) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Meal m = meals.findByIdAndUserId(mealId, userId)
                .orElseThrow(() -> new MealNotFoundException(mealId));
        meals.deleteByIdAndUserId(m.getId(), userId);
    }

    @Transactional(readOnly = true)
    public MealResponse getOne(Jwt jwt, UUID mealId) {
        UUID userId = UUID.fromString(jwt.getSubject());
        Meal m = meals.findByIdAndUserId(mealId, userId)
                .orElseThrow(() -> new MealNotFoundException(mealId));
        return toResponse(m);
    }

    @Transactional(readOnly = true)
    public List<MealResponse> getByDateRange(Jwt jwt, LocalDate from, LocalDate to) {
        if (from == null || to == null) {
            throw new InvalidMealException("from y to son obligatorios");
        }
        UUID userId = UUID.fromString(jwt.getSubject());

        OffsetDateTime fromDt = from.atStartOfDay().atOffset(ZoneOffset.UTC);
        OffsetDateTime toDt = to.plusDays(1).atStartOfDay().atOffset(ZoneOffset.UTC).minusNanos(1);

        return meals.findByUserIdAndLoggedAtBetween(userId, fromDt, toDt)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // --------- helpers privados ---------

    private MealType parseMealType(String raw) {
        try {
            return MealType.fromString(raw);
        } catch (IllegalArgumentException ex) {
            throw new InvalidMealException("mealType inválido: " + raw);
        }
    }

    private MealType parseMealTypeOrDefault(String raw, MealType def) {
        if (raw == null || raw.isBlank()) return def;
        return parseMealType(raw);
    }

    private Set<FoodCategory> resolveCategories(Set<Integer> ids) {
        if (ids == null || ids.isEmpty()) return Set.of();
        List<FoodCategory> found = categories.findAllById(ids);
        if (found.size() != ids.size()) {
            throw new FoodCategoryNotFoundException();
        }
        return new HashSet<>(found);
    }

    private MealResponse toResponse(Meal m) {
        Set<Integer> categoryIds = m.getCategories().stream()
                .map(FoodCategory::getId)
                .collect(Collectors.toSet());

        return new MealResponse(
                m.getId(),
                m.getUserId(),
                m.getDescription(),
                m.getCalories(),
                m.getProteinGrams(),
                m.getCarbsGrams(),
                m.getFatGrams(),
                m.getMealType() != null ? m.getMealType().name() : null,
                m.getLoggedAt(),
                m.getCreatedAt(),
                categoryIds
        );
    }
}
