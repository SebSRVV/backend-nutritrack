package com.sebsrvv.app.modules.profile.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.*;
import java.util.UUID;

/**
 * Tabla: profiles
 * PK = id (FK a auth.users.id)
 */
@Entity @Table(name = "profiles")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class Profile {
    @Id
    @Column(nullable = false)
    private UUID id;                

    private String username;

    private LocalDate dob;          
    private String sex;              

    private Integer height_cm;       
    @Column(precision = 10, scale = 2)
    private Double weight_kg;      
    @Column(precision = 10, scale = 2)
    private Double bmi;

    private String activity_level;   
    private String diet_type;        

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
