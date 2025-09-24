package com.sebsrvv.app.modules.practices.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "practice_logs")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class PracticeLog {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;
    @Column(nullable=false)
    private UUID user_id;
    @Column(nullable=false)
    private UUID practice_id;
    private OffsetDateTime logged_at;
}
