package com.sebsrvv.app.modules.meals.domain;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

public class Meal {

    private final UUID id;
    private final UUID userId;

    private String description;
    private int calories;
    private BigDecimal proteinGrams;
    private BigDecimal carbsGrams;
    private BigDecimal fatGrams;
    private MealType mealType;
    private OffsetDateTime loggedAt;
    private final OffsetDateTime createdAt;

    // relación con food_categories (via meal_log_categories)
    private final Set<Integer> categoryIds = new HashSet<>();

    public Meal(UUID id,
                UUID userId,
                String description,
                int calories,
                BigDecimal proteinGrams,
                BigDecimal carbsGrams,
                BigDecimal fatGrams,
                MealType mealType,
                OffsetDateTime loggedAt,
                OffsetDateTime createdAt,
                Set<Integer> categoryIds) {

        Objects.requireNonNull(userId, "userId es obligatorio");
        Objects.requireNonNull(description, "description es obligatorio");
        Objects.requireNonNull(mealType, "mealType es obligatorio");

        if (description.isBlank()) {
            throw new IllegalArgumentException("description no puede ser vacío");
        }
        if (calories < 0) {
            throw new IllegalArgumentException("calories no puede ser negativo");
        }

        this.id = id != null ? id : UUID.randomUUID();
        this.userId = userId;
        this.description = description;
        this.calories = calories;
        this.proteinGrams = proteinGrams;
        this.carbsGrams = carbsGrams;
        this.fatGrams = fatGrams;
        this.mealType = mealType;
        this.loggedAt = loggedAt != null ? loggedAt : OffsetDateTime.now();
        this.createdAt = createdAt != null ? createdAt : OffsetDateTime.now();

        if (categoryIds != null) {
            this.categoryIds.addAll(categoryIds);
        }
    }

    // Factory para crear nuevos meals
    public static Meal create(UUID userId,
                              String description,
                              int calories,
                              BigDecimal proteinGrams,
                              BigDecimal carbsGrams,
                              BigDecimal fatGrams,
                              MealType mealType,
                              OffsetDateTime loggedAt,
                              Set<Integer> categoryIds) {

        return new Meal(
                null,
                userId,
                description,
                calories,
                proteinGrams,
                carbsGrams,
                fatGrams,
                mealType,
                loggedAt,
                null,
                categoryIds
        );
    }

    // --------- comportamiento de dominio ---------

    public void update(String description,
                       Integer calories,
                       BigDecimal proteinGrams,
                       BigDecimal carbsGrams,
                       BigDecimal fatGrams,
                       MealType mealType,
                       OffsetDateTime loggedAt,
                       Set<Integer> newCategoryIds) {

        if (description != null && !description.isBlank()) {
            this.description = description;
        }
        if (calories != null) {
            if (calories < 0) {
                throw new IllegalArgumentException("calories no puede ser negativo");
            }
            this.calories = calories;
        }
        if (proteinGrams != null) {
            this.proteinGrams = proteinGrams;
        }
        if (carbsGrams != null) {
            this.carbsGrams = carbsGrams;
        }
        if (fatGrams != null) {
            this.fatGrams = fatGrams;
        }
        if (mealType != null) {
            this.mealType = mealType;
        }
        if (loggedAt != null) {
            this.loggedAt = loggedAt;
        }
        if (newCategoryIds != null) {
            this.categoryIds.clear();
            this.categoryIds.addAll(newCategoryIds);
        }
    }

    // --------- getters -------------

    public UUID getId() {
        return id;
    }

    public UUID getUserId() {
        return userId;
    }

    public String getDescription() {
        return description;
    }

    public int getCalories() {
        return calories;
    }

    public BigDecimal getProteinGrams() {
        return proteinGrams;
    }

    public BigDecimal getCarbsGrams() {
        return carbsGrams;
    }

    public BigDecimal getFatGrams() {
        return fatGrams;
    }

    public MealType getMealType() {
        return mealType;
    }

    public OffsetDateTime getLoggedAt() {
        return loggedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public Set<Integer> getCategoryIds() {
        return Set.copyOf(categoryIds);
    }
}
