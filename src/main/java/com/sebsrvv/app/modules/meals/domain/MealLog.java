package com.sebsrvv.app.modules.meals.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@Entity
@Table(name = "meal_logs")
public class MealLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // userId as String to support UUIDs from Supabase
    @Column(name = "user_id", nullable = false, length = 100)
    private String userId;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private Double calories;

    @Column(nullable = false)
    private Double protein_g = 0.0;

    @Column(nullable = false)
    private Double carbs_g = 0.0;

    @Column(nullable = false)
    private Double fat_g = 0.0;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "meal_type", nullable = false)
    private MealType mealType;

    @Column(name = "logged_at", nullable = false)
    private Instant loggedAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @ElementCollection
    @CollectionTable(name = "meal_items", joinColumns = @JoinColumn(name = "meal_log_id"))
    @Column(name = "item")
    private List<String> mealItems = new ArrayList<>();
}
