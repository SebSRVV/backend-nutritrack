package com.sebsrvv.app.modules.goals.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "user_goals")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserGoal {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable = false)
    private UUID user_id;
    private String goal_name;
    private String goal_type;
    @Column(precision = 12, scale = 2)
    private Double target_value;
    @Column(precision = 12, scale = 2)
    private Double current_progress;
    private String unit;
    @ManyToOne
    @JoinColumn(name = "category_id")
    private FoodCategory category;
    private OffsetDateTime created_at;
    private OffsetDateTime updated_at;

    @PrePersist
    void prePersist(){
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        created_at = now;
        updated_at = now;
        if(current_progress == null) current_progress = 0.0;
    }
    @PreUpdate
    void preUpdate(){
        updated_at = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
