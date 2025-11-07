package com.sebsrvv.app.modules.goals.domain;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GoalRepository extends JpaRepository<Goal, UUID> {

    // Listar metas del usuario (orden reciente primero)
    List<Goal> findByUserIdOrderByCreatedAtDesc(UUID userId);

    // Asegurar propiedad del recurso (RN-06)
    Optional<Goal> findByIdAndUserId(UUID id, UUID userId);
}
