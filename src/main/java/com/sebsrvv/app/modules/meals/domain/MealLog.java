package com.sebsrvv.app.modules.meals.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "meal_logs")
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 128)
    private String userId;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column
    private Double calories;

    @Column
    private Double protein_g;

    @Column
    private Double carbs_g;

    @Column
    private Double fat_g;

    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt;

    // <-- aquí forzamos columnDefinition para que Hibernate use el tipo enum de Postgres
    @Enumerated(EnumType.STRING)
    @Column(name = "meal_type", columnDefinition = "meal_type")
    private MealType mealType;

    @ElementCollection
    @CollectionTable(name = "meal_log_items", joinColumns = @JoinColumn(name = "meal_id"))
    @Column(name = "item", length = 200)
    private List<String> mealItems = new ArrayList<>();

    public MealLog() {}

    // getters / setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getCalories() { return calories; }
    public void setCalories(Double calories) { this.calories = calories; }

    public Double getProtein_g() { return protein_g; }
    public void setProtein_g(Double protein_g) { this.protein_g = protein_g; }

    public Double getCarbs_g() { return carbs_g; }
    public void setCarbs_g(Double carbs_g) { this.carbs_g = carbs_g; }

    public Double getFat_g() { return fat_g; }
    public void setFat_g(Double fat_g) { this.fat_g = fat_g; }

    public Instant getLoggedAt() { return loggedAt; }
    public void setLoggedAt(Instant loggedAt) { this.loggedAt = loggedAt; }

    public MealType getMealType() { return mealType; }
    public void setMealType(MealType mealType) { this.mealType = mealType; }

    public List<String> getMealItems() { return mealItems; }
    public void setMealItems(List<String> mealItems) {
        this.mealItems = mealItems == null ? new ArrayList<>() : mealItems;
    }

    public void addMealItem(String item) {
        if (this.mealItems == null) this.mealItems = new ArrayList<>();
        this.mealItems.add(item);
    }

    public void removeMealItem(String item) {
        if (this.mealItems != null) this.mealItems.remove(item);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MealLog)) return false;
        MealLog mealLog = (MealLog) o;
        return Objects.equals(id, mealLog.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
