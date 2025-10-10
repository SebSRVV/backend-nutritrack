// modules/practices/infra/SupabasePracticeProgressDetailRepository.java
package com.sebsrvv.app.modules.practices.infra;

import com.sebsrvv.app.modules.practices.domain.port.PracticeProgressDetailPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.*;

@Repository
public class SupabasePracticeProgressDetailRepository implements PracticeProgressDetailPort {

    private final SupabaseDataClient supa;

    public SupabasePracticeProgressDetailRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public Practice findPractice(UUID userId, UUID practiceId) {
        String qp = "select=id,practice_name,frequency_target,is_active"
                + "&id=eq." + practiceId
                + "&user_id=eq." + userId
                + "&limit=1";

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rows = (List<Map<String,Object>>)(List<?>)
                supa.select("healthy_practices", qp).blockOptional().orElse(List.of());

        if (rows.isEmpty()) return null;
        var r = rows.get(0);
        return new Practice(
                UUID.fromString((String) r.get("id")),
                (String) r.get("practice_name"),
                r.get("frequency_target") == null ? 0 : ((Number) r.get("frequency_target")).intValue(),
                (Boolean) r.getOrDefault("is_active", Boolean.TRUE)
        );
    }

    @Override
    public List<Log> findLogs(UUID userId, UUID practiceId, LocalDate from, LocalDate to) {
        String qp = "select=id,logged_at,logged_date"
                + "&user_id=eq." + userId
                + "&practice_id=eq." + practiceId
                + "&logged_date=gte." + from
                + "&logged_date=lte." + to
                + "&order=logged_at.asc";

        @SuppressWarnings("unchecked")
        List<Map<String,Object>> rows = (List<Map<String,Object>>)(List<?>)
                supa.select("practice_logs", qp).blockOptional().orElse(List.of());

        List<Log> out = new ArrayList<>();
        for (var r : rows) {
            out.add(new Log(
                    UUID.fromString((String) r.get("id")),
                    OffsetDateTime.parse((String) r.get("logged_at")),
                    LocalDate.parse((String) r.get("logged_date"))
            ));
        }
        return out;
    }
}
