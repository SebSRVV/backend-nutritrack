// modules/goals/infra/SupabaseGoalDeleteRepository.java
package com.sebsrvv.app.modules.goals.infra;

import com.sebsrvv.app.modules.goals.domain.port.GoalDeleteCommandPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.*;

@Repository
public class SupabaseGoalDeleteRepository implements GoalDeleteCommandPort {

    private final SupabaseDataClient supa;

    public SupabaseGoalDeleteRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public DeletedGoal delete(UUID userId, UUID goalId, boolean soft) {
        String filter = "id=eq." + goalId + "&user_id=eq." + userId;

        if (soft) {
            Map<String,Object> body = Map.of(
                    "is_active", false,
                    "updated_at", OffsetDateTime.now().toString()
            );

            List<Map<String,Object>> rows = supa.patch("user_goals", filter, body)
                    .blockOptional()
                    .orElse(Collections.emptyList());

            if (rows.isEmpty()) throw new NoSuchElementException("Goal no encontrado");
            Map<String,Object> r = rows.get(0);
            return new DeletedGoal(UUID.fromString((String) r.get("id")), false);

        } else {
            // Integer (no primitivo) para poder chequear null
            Integer status = supa.delete("user_goals", filter).block();
            if (status == null || status < 200 || status >= 300) {
                throw new IllegalStateException("No se pudo eliminar (status=" + status + ")");
            }
            return new DeletedGoal(goalId, false);
        }
    }
}
