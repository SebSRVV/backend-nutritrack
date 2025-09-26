package com.sebsrvv.app.modules.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

/**
 * Tabla: user_recommendations
 * PK = user_id (FK a auth.users.id)
 */
@Entity @Table(name = "user_recommendations")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserRecommendation {
    @Id
    @Column(nullable = false)
    private UUID user_id;             

    private String sex;
    private LocalDate dob;
    private Integer age_years;

    private Integer height_cm;
    @Column(precision = 10, scale = 2)
    private Double weight_kg;

    private String activity_level;
    @Column(precision = 6, scale = 3)
    private Double activity_factor;

    private String diet_type;
    @Column(precision = 6, scale = 3)
    private Double diet_adjustment;      

    private Integer water_factor_ml_per_kg;
    private Integer water_activity_bonus_ml; 

    @Column(precision = 10, scale = 2) private Double bmr_kcal;
    @Column(precision = 10, scale = 2) private Double tdee_kcal;
    private Integer goal_kcal;
    private Integer water_ml;

    private String method;             

    private OffsetDateTime created_at;
    private OffsetDateTime updated_at;

    @PrePersist
    void prePersist() {
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        created_at = now;
        updated_at = now;
    }
    @PreUpdate
    void preUpdate() {
        updated_at = OffsetDateTime.now(ZoneOffset.UTC);
    }
}
