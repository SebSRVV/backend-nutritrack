package com.sebsrvv.app.modules.meals.domain;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

/**
 * Entidad que representa una comida (Meal) registrada por un usuario.
 */
@Entity
@Table(name = "meals")
@Data
public class Meal {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private UUID userId; // ID del usuario (relación lógica con perfil en Supabase)

    @Column(nullable = false)
    private String name; // Nombre de la comida, por ejemplo: “Desayuno saludable”

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MealType mealType; // Tipo de comida (BREAKFAST, LUNCH, DINNER, SNACK)

    @Column(nullable = false)
    private Double calories; // Calorías totales del plato

    @Column
    private String note; // Nota opcional (por ejemplo, “pollo a la plancha con ensalada”)

    @Column(nullable = false)
    private LocalDate loggedAt; // Fecha del registro

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private FoodCategory category; // Relación con categoría de alimento
}
