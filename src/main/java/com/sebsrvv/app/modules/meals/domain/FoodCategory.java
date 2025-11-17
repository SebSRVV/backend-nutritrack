package com.sebsrvv.app.modules.meals.domain;

public class FoodCategory {

    private final Integer id;
    private final String name;
    private final String description;

    public FoodCategory(Integer id, String name, String description) {
        if (id == null) {
            throw new IllegalArgumentException("id es obligatorio");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("name es obligatorio");
        }
        this.id = id;
        this.name = name;
        this.description = description;
    }

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
