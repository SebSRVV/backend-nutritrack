// modules/practices/infra/SupabasePracticeProgressRepository.java
package com.sebsrvv.app.modules.practices.infra;

import com.sebsrvv.app.modules.practices.domain.port.PracticeProgressQueryPort;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
public class SupabasePracticeProgressRepository implements PracticeProgressQueryPort {

    private final SupabaseDataClient supa;

    public SupabasePracticeProgressRepository(SupabaseDataClient supa) {
        this.supa = supa;
    }

    @Override
    public List<UserPractice> findUserPractices(UUID userId) {
        // Traemos columnas necesarias del usuario
        String qp = "select=id,default_id,practice_name,frequency_target,is_active&user_id=eq." + userId;
        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) (List<?>)
                supa.select("healthy_practices", qp).blockOptional().orElse(Collections.emptyList());

        List<UserPractice> list = new ArrayList<>();
        for (var r : rows) {
            list.add(new UserPractice(
                    UUID.fromString((String) r.get("id")),
                    r.get("default_id") == null ? null : ((Number) r.get("default_id")).intValue(),
                    (String) r.getOrDefault("practice_name", null),
                    r.get("frequency_target") == null ? 0 : ((Number) r.get("frequency_target")).intValue(),
                    (Boolean) r.getOrDefault("is_active", Boolean.TRUE)
            ));
        }
        return list;
    }

    @Override
    public Map<UUID, Map<LocalDate, Integer>> findDailyCounts(
            UUID userId, Collection<UUID> practiceIds, LocalDate from, LocalDate to) {

        if (practiceIds.isEmpty()) return Collections.emptyMap();

        String inPractice = "(" + practiceIds.stream().map(UUID::toString).collect(Collectors.joining(",")) + ")";

        String qp = "select=practice_id,logged_date&user_id=eq." + userId
                + "&practice_id=in." + inPractice
                + "&logged_date=gte." + from
                + "&logged_date=lte." + to;

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> rows = (List<Map<String, Object>>) (List<?>)
                supa.select("practice_logs", qp).blockOptional().orElse(Collections.emptyList());

        Map<UUID, Map<LocalDate, Integer>> out = new HashMap<>();
        for (var r : rows) {
            UUID pid = UUID.fromString((String) r.get("practice_id"));
            LocalDate date = LocalDate.parse((String) r.get("logged_date"));
            out.computeIfAbsent(pid, k -> new HashMap<>())
                    .merge(date, 1, Integer::sum);
        }
        return out;
    }
}
