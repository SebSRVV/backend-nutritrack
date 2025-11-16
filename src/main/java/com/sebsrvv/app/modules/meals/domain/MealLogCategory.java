package com.sebsrvv.app.modules.meals.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "meal_log_categories")
public class MealLogCategory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String name;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "meal_id")
    private MealLog meal;

    public MealLogCategory() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public MealLog getMeal() { return meal; }
    public void setMeal(MealLog meal) { this.meal = meal; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof MealLogCategory)) return false;
        MealLogCategory that = (MealLogCategory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
