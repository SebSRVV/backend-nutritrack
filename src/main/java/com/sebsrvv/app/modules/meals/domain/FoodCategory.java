package com.sebsrvv.app.modules.meals.domain;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "food_categories")
public class FoodCategory {

    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    @Column(name = "description")
    private String description;

    // Lado inverso opcional (no hace falta usarlo)
    @ManyToMany(mappedBy = "categories")
    private Set<Meal> meals = new HashSet<>();

    public Integer getId() { return id; }
    public void setId(Integer id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Set<Meal> getMeals() { return meals; }
    public void setMeals(Set<Meal> meals) { this.meals = meals; }
}
