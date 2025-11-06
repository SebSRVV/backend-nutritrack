package com.sebsrvv.app.modules.meals.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.UUID;

/**
 * Categoría general de los alimentos (por ejemplo, “Proteínas”, “Carbohidratos”).
 */
@Entity
@Table(name = "food_categories")
@Data
public class FoodCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false, unique = true)
    private String name; // Nombre de la categoría (ejemplo: "Proteínas")

    @Column
    private String description; // Descripción opcional
}
