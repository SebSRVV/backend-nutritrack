// modules/goals/infra/SupabaseGoalProgressRepository.java
package com.sebsrvv.app.modules.goals.infra;

import com.sebsrvv.app.modules.goals.domain.model.GoalProgress;
import com.sebsrvv.app.modules.goals.domain.port.GoalProgressCommandPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Repository
public class SupabaseGoalProgressRepository implements GoalProgressCommandPort {

    private final SupabaseDataClient supa;

    public SupabaseGoalProgressRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public GoalProgress upsert(UUID userId, UUID goalId, LocalDate logDate, int value, String note) {
        Map<String,Object> row = new HashMap<>();
        row.put("user_id", userId);
        row.put("goal_id", goalId);
        row.put("log_date", logDate.toString());
        row.put("value", value);
        if (note != null && !note.isBlank()) row.put("note", note);

        // upsert con Prefer: resolution=merge-duplicates ya configurado en tu cliente
        @SuppressWarnings("unchecked")
        List<Map<String,Object>> res = (List<Map<String,Object>>)(List<?>)
                supa.upsert("user_goal_progress", row)
                        .blockOptional().orElse(Collections.emptyList());

        if (res.isEmpty()) throw new IllegalStateException("No se pudo upsert el progreso");

        Map<String,Object> r = res.get(0);

        return new GoalProgress(
                UUID.fromString((String) r.get("id")),
                UUID.fromString((String) r.get("user_id")),
                UUID.fromString((String) r.get("goal_id")),
                LocalDate.parse((String) r.get("log_date")),
                ((Number) r.get("value")).intValue(),
                (String) r.get("note"),
                OffsetDateTime.parse((String) r.get("created_at")),
                OffsetDateTime.parse((String) r.get("updated_at"))
        );
    }
}
