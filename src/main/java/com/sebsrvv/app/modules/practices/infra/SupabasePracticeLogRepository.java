// infrastructure/adapter/SupabasePracticeLogRepository.java
package com.sebsrvv.app.modules.practices.infrastructure.adapter;

import com.sebsrvv.app.modules.practices.domain.model.PracticeLog;
import com.sebsrvv.app.modules.practices.domain.port.PracticeLogCommandPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.time.*;
import java.util.*;

@Repository
public class SupabasePracticeLogRepository implements PracticeLogCommandPort {

    private final SupabaseDataClient supa;

    public SupabasePracticeLogRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public PracticeLog create(UUID userId, UUID practiceId, OffsetDateTime loggedAt, String note) {
        Map<String,Object> row = new HashMap<>();
        row.put("user_id", userId);
        row.put("practice_id", practiceId);
        row.put("logged_at", loggedAt.toString()); // ISO-8601
        if (note != null && !note.isBlank()) row.put("note", note);

        // Prefer: return=representation ya está en el cliente
        List<Map<String,Object>> res = supa.insert("practice_logs", row)
                .blockOptional()
                .orElse(Collections.emptyList());

        if (res.isEmpty()) throw new IllegalStateException("No se pudo insertar el log");

        Map<String,Object> r = res.get(0);

        UUID id = UUID.fromString((String) r.get("id"));
        UUID uid = UUID.fromString((String) r.get("user_id"));
        UUID pid = UUID.fromString((String) r.get("practice_id"));

        // Los timestamps vienen como String ISO-8601
        OffsetDateTime loggedAtOut = OffsetDateTime.parse((String) r.get("logged_at"));
        OffsetDateTime createdAt = OffsetDateTime.parse((String) r.get("created_at"));

        // logged_date puede venir null si la vista no lo devuelve; lo calculamos si falta
        String ld = (String) r.get("logged_date");
        LocalDate loggedDate = (ld != null) ? LocalDate.parse(ld) : loggedAtOut.toLocalDate();

        String noteOut = (StPracticeLogRequest.javaring) r.get("note");

        return new PracticeLog(id, uid, pid, loggedAtOut, loggedDate, noteOut, createdAt);
    }
}
