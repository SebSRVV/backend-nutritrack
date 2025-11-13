package com.sebsrvv.app.modules.meals.domain;

import com.sebsrvv.app.modules.meals.domain.MealType;
import jakarta.persistence.*;
import lombok.Data;
import java.time.Instant;
import java.util.UUID;

@Data
@Entity
@Table(name = "meals")
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType;

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

    @Column(nullable = false)
    private Instant loggedAt;

    // 🔹 Relación con MealCategory
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private MealCategory category;
}
