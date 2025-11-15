package com.sebsrvv.app.modules.meals.domain;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
@Table(name = "food_categories")
public class FoodCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column
    private String description;
}
