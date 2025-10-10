// modules/practices/infra/SupabasePracticeDeleteRepository.java
package com.sebsrvv.app.modules.practices.infra;

import com.sebsrvv.app.modules.practices.domain.port.PracticeDeleteCommandPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.*;

@Repository
public class SupabasePracticeDeleteRepository implements PracticeDeleteCommandPort {

    private final SupabaseDataClient supa;

    public SupabasePracticeDeleteRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public DeletedPractice delete(UUID userId, UUID practiceId, boolean soft) {
        if (soft) {
            Map<String,Object> body = Map.of(
                    "is_active", false,
                    "updated_at", OffsetDateTime.now().toString()
            );
            String qp = "id=eq." + practiceId + "&user_id=eq." + userId;
            @SuppressWarnings("unchecked")
            List<Map<String,Object>> rows = (List<Map<String,Object>>)(List<?>)
                    supa.patch("healthy_practices", qp, body)
                            .blockOptional().orElse(Collections.emptyList());

            if (rows.isEmpty()) throw new NoSuchElementException("Práctica no encontrada");
            Map<String,Object> r = rows.get(0);
            return new DeletedPractice(UUID.fromString((String) r.get("id")), false);

        } else {
            // hard delete
            String qp = "id=eq." + practiceId + "&user_id=eq." + userId;
            int status = supa.delete("healthy_practices", qp).block();
            if (status < 200 || status >= 300) throw new IllegalStateException("No se pudo eliminar");
            return new DeletedPractice(practiceId, false);
        }
    }
}
