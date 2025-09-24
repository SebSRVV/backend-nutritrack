package com.sebsrvv.app.modules.meals.entity;

import com.sebsrvv.app.modules.goals.entity.FoodCategory;
import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "meal_log_categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MealLogCategory {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(optional=false)
    @JoinColumn(name="meal_log_id")
    private MealLog mealLog;

    @ManyToOne(optional=false)
    @JoinColumn(name="category_id")
    private FoodCategory category;
}
