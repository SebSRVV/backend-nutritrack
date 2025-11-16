package com.sebsrvv.app.modules.meals.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name = "meal_logs")
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(nullable = false, length = 1000)
    private String description;

    @Column(nullable = false)
    private Double calories;

    @Column(nullable = false)
    private Double protein_g = 0.0;

    @Column(nullable = false)
    private Double carbs_g = 0.0;

    @Column(nullable = false)
    private Double fat_g = 0.0;

    // STRING en DB → QUITAMOS @Data para evitar setters automáticos erróneos
    @Column(name = "meal_type", nullable = false, length = 50)
    private String mealType;

    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @ElementCollection
    @CollectionTable(name = "meal_items", joinColumns = @JoinColumn(name = "meal_log_id"))
    @Column(name = "item")
    private List<String> mealItems = new ArrayList<>();

    // Setter correcto para ENUM → String lowercase
    public void setMealType(MealType type) {
        this.mealType = (type == null) ? null : type.name().toLowerCase();
    }

    // Getter correcto ENUM
    public MealType getMealTypeEnum() {
        return (mealType == null) ? null : MealType.valueOf(mealType.toUpperCase());
    }

    // Setter correcto para Instant
    public void setLoggedAt(Instant instant) {
        this.loggedAt = instant;
    }
}
