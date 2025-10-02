package com.sebsrvv.app.modules.meals.entity;

import com.sebsrvv.app.modules.goals.entity.FoodCategory;
import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "meal_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MealLog {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable=false)
    private UUID user_id;
    private String description;
    private Integer calories;
    @Column(precision=12, scale=2)
    private Double protein_g;
    @Column(precision=12, scale=2)
    private Double carbs_g;
    @Column(precision=12, scale=2)
    private Double fat_g;
    private String meal_type;
    private OffsetDateTime logged_at;
    private OffsetDateTime created_at;
    private String meal_categories;
    @Column(columnDefinition = "jsonb")
    private String ai_items;

    @PrePersist
    void prePersist(){
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        created_at = now;
        if (logged_at == null) logged_at = now;
    }
}
