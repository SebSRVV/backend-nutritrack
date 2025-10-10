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
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> rows = (List<Map<String,Object>>)(List<?>)
                    supa.patch("user_goals", filter, body)
                            .blockOptional().orElse(Collections.emptyList());

            if (rows.isEmpty()) throw new NoSuchElementException("Goal no encontrado");
            var r = rows.get(0);
            return new DeletedGoal(UUID.fromString((String) r.get("id")), false);
        } else {
            int status = supa.delete("user_goals", filter).block();
            if (status == null || status < 200 || status >= 300)
                throw new IllegalStateException("No se pudo eliminar");
            return new DeletedGoal(goalId, false);
        }
    }
}
