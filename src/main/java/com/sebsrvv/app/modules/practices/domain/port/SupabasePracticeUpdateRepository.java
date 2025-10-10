// modules/practices/infra/SupabasePracticeUpdateRepository.java
package com.sebsrvv.app.modules.practices.infra;

import com.sebsrvv.app.modules.practices.domain.port.PracticeUpdateCommandPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.*;

@Repository
public class SupabasePracticeUpdateRepository implements PracticeUpdateCommandPort {

    private final SupabaseDataClient supa;

    public SupabasePracticeUpdateRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public UpdatedPractice update(UUID userId, UUID practiceId, UpdateFields f) {
        Map<String,Object> body = new HashMap<>();
        if (f.practiceName() != null)    body.put("practice_name", f.practiceName());
        if (f.description() != null)     body.put("description", f.description());
        if (f.icon() != null)            body.put("icon", f.icon());
        if (f.frequencyTarget() != null) body.put("frequency_target", f.frequencyTarget());
        if (f.isActive() != null)        body.put("is_active", f.isActive());

        // Si tu DB no actualiza updated_at automáticamente, descomenta:
        // body.put("updated_at", OffsetDateTime.now().toString());

        // Filtro: asegurar que pertenece al usuario
        String qp = "id=eq." + practiceId + "&user_id=eq." + userId;

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rows = (List<Map<String,Object>>)(List<?>)
                supa.patch("healthy_practices", qp, body)
                        .blockOptional().orElse(Collections.emptyList());

        if (rows.isEmpty()) {
            throw new NoSuchElementException("Práctica no encontrada para el usuario");
        }

        Map<String,Object> r = rows.get(0);

        return new UpdatedPractice(
                UUID.fromString((String) r.get("id")),
                (String) r.get("practice_name"),
                (String) r.get("description"),
                (String) r.get("icon"),
                r.get("frequency_target") == null ? null : ((Number) r.get("frequency_target")).intValue(),
                r.get("is_active") == null ? null : (Boolean) r.get("is_active"),
                (String) r.get("updated_at") // ISO-8601 de la DB
        );
    }
}
