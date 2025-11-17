package com.sebsrvv.app.modules.meals.infra;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "meal_logs")  // schema = "public" si lo necesitas explícito
public class MealEntity {

    @Id
    @Column(name = "id", nullable = false)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "calories", nullable = false)
    private int calories;

    @Column(name = "protein_g")
    private BigDecimal proteinGrams;

    @Column(name = "carbs_g")
    private BigDecimal carbsGrams;

    @Column(name = "fat_g")
    private BigDecimal fatGrams;

    @Column(name = "meal_type", nullable = false)
    private String mealType; // mapearemos al enum domain.MealType a mano

    @Column(name = "logged_at", nullable = false)
    private OffsetDateTime loggedAt;

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    // relación con food_categories via meal_log_categories
    @ManyToMany
    @JoinTable(
            name = "meal_log_categories",
            joinColumns = @JoinColumn(name = "meal_log_id"),
            inverseJoinColumns = @JoinColumn(name = "category_id")
    )
    private Set<FoodCategoryEntity> categories = new HashSet<>();

    // ---------- getters/setters obligatorios para JPA ------------

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getUserId() {
        return userId;
    }

    public void setUserId(UUID userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getCalories() {
        return calories;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public BigDecimal getProteinGrams() {
        return proteinGrams;
    }

    public void setProteinGrams(BigDecimal proteinGrams) {
        this.proteinGrams = proteinGrams;
    }

    public BigDecimal getCarbsGrams() {
        return carbsGrams;
    }

    public void setCarbsGrams(BigDecimal carbsGrams) {
        this.carbsGrams = carbsGrams;
    }

    public BigDecimal getFatGrams() {
        return fatGrams;
    }

    public void setFatGrams(BigDecimal fatGrams) {
        this.fatGrams = fatGrams;
    }

    public String getMealType() {
        return mealType;
    }

    public void setMealType(String mealType) {
        this.mealType = mealType;
    }

    public OffsetDateTime getLoggedAt() {
        return loggedAt;
    }

    public void setLoggedAt(OffsetDateTime loggedAt) {
        this.loggedAt = loggedAt;
    }

    public OffsetDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(OffsetDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public Set<FoodCategoryEntity> getCategories() {
        return categories;
    }

    public void setCategories(Set<FoodCategoryEntity> categories) {
        this.categories = categories;
    }
}
