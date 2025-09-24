package com.sebsrvv.app.modules.practices.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Entity
@Table(name = "healthy_practices")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class HealthyPractice {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable=false)
    private UUID user_id;
    private String practice_name;
    private String description;
    private String icon;
    private Integer frequency_target;
    private OffsetDateTime created_at;
    private OffsetDateTime updated_at;

    @PrePersist
    void prePersist(){
        var now = OffsetDateTime.now(ZoneOffset.UTC);
        created_at = now; updated_at = now;
    }
    @PreUpdate
    void preUpdate(){ updated_at = OffsetDateTime.now(ZoneOffset.UTC); }
}
