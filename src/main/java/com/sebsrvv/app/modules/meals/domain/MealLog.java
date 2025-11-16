package com.sebsrvv.app.modules.meals.domain;

import jakarta.persistence.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "meal_logs")
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String userId; // guardamos el subject del JWT como String

    @Column(name = "meal_type")
    private String mealType; // almacenamos como string (ej: "BREAKFAST")

    @Column(columnDefinition = "text")
    private String description;

    private Double calories;
    private Double protein_g;
    private Double carbs_g;
    private Double fat_g;

    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt;

    @ElementCollection
    @CollectionTable(name = "meal_log_items", joinColumns = @JoinColumn(name = "meal_log_id"))
    @Column(name = "item")
    private List<String> mealItems = new ArrayList<>();

    // --- Getters / Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getMealType() { return mealType; }
    public void setMealType(String mealType) { this.mealType = mealType; }

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

    public List<String> getMealItems() { return mealItems; }
    public void setMealItems(List<String> mealItems) {
        this.mealItems = mealItems == null ? new ArrayList<>() : mealItems;
    }
}
