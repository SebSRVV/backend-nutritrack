// src/main/java/com/sebsrvv/app/modules/goals/application/UpsertGoalProgressUseCase.java
package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.web.dto.GoalProgressResponse;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Service
public class UpsertGoalProgressUseCase {

    private final SupabaseDataClient supabase;

    public UpsertGoalProgressUseCase(SupabaseDataClient supabase) {
        this.supabase = supabase;
    }

    /**
     * Inserta o actualiza el progreso de una meta mediante un RPC reenviando el JWT del usuario.
     *
     * @param authorizationBearer JWT del usuario (ej: "Bearer eyJ...")
     * @param goalId ID de la meta
     * @param logDate Fecha del registro (yyyy-MM-dd)
     * @param value Valor del progreso
     * @param note Nota opcional
     * @return Objeto GoalProgressResponse con el resultado del RPC
     */
    public GoalProgressResponse executeViaRpcForwardJwt(
            String authorizationBearer,
            UUID goalId,
            LocalDate logDate,
            Integer value,
            String note
    ) {

        Map<String, Object> payload = new HashMap<>();
        payload.put("goal_id", goalId.toString());
        payload.put("log_date", logDate.toString());
        payload.put("value", value);
        payload.put("note", note);


        // Llama al RPC api_insert_goal_progress reenviando el token JWT del usuario
        return supabase.callRpc(
                        "api_insert_goal_progress",
                        payload,
                        authorizationBearer,
                        new ParameterizedTypeReference<GoalProgressResponse>() {}
                )
                .switchIfEmpty(Mono.error(new IllegalStateException("RPC returned empty body")))
                .onErrorMap(ex -> new RuntimeException("Supabase RPC error: " + ex.getMessage(), ex))
                .block(); // Bloquea el Mono para devolver el resultado sincrónicamente
    }
}
