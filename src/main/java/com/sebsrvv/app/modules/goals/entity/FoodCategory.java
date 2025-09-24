package com.sebsrvv.app.modules.goals.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "food_categories")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class FoodCategory {
    @Id
    private Integer id;
    private String name;
    private String description;
}
