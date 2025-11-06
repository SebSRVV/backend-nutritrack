package com.sebsrvv.app.modules.meals.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

/**
 * Entidad que representa una categoría de comida (Ejemplo: Desayuno, Almuerzo, Cena, Snack).
 */
@Data
@Entity
@Table(name = "meal_categories")
public class MealCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    /**
     * Nombre de la categoría (único).
     */
    @Column(nullable = false, unique = true)
    private String name;

    /**
     * Lista de comidas pertenecientes a esta categoría.
     */
    @OneToMany(mappedBy = "category", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meal> meals;
}
