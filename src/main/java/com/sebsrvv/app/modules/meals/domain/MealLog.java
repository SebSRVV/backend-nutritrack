package com.sebsrvv.app.modules.meals.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@Entity
@Table(name = "meal_logs")
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double calories;

    @Column(nullable = false)
    private Double protein_g;

    @Column(nullable = false)
    private Double carbs_g;

    @Column(nullable = false)
    private Double fat_g;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @ElementCollection
    @Column(name = "meal_items")
    private List<String> mealItems = new ArrayList<>();
}
