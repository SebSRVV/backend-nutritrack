// src/main/java/com/sebsrvv/app/modules/practices/PracticeService.java
package com.sebsrvv.app.modules.practices.application;

import com.sebsrvv.app.modules.practices.web.dto.PracticeDto;
import com.sebsrvv.app.modules.practices.web.dto.PracticeEntryDto;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.*;

@Service
public class PracticeService {

    private final SupabaseDataClient data;

    public PracticeService(SupabaseDataClient data) {
        this.data = data;
    }

    //Mapa string de variables en PostreSQL
    private static Map<String, Object> toRow(PracticeDto dto) {
        Map<String,Object> m = new HashMap<>();
        if (dto.name() != null) m.put("name", dto.name());
        if (dto.description() != null) m.put("description", dto.description());
        if (dto.icon() != null) m.put("icon", dto.icon());
        if (dto.value_kind() != null) m.put("value_kind", dto.value_kind());
        if (dto.target_value() != null) m.put("target_value", dto.target_value());
        if (dto.target_unit() != null) m.put("target_unit", dto.target_unit());
        if (dto.operator() != null) m.put("operator", dto.operator());
        if (dto.days_per_week() != null) m.put("days_per_week", dto.days_per_week());
        if (dto.is_active() != null) m.put("is_active", dto.is_active());
        return m;
    }

    private static Map<String, Object> toRow(PracticeEntryDto dto) {
        Map<String,Object> m = new HashMap<>();
        if (dto.practice_id() != null) m.put("practice_id", dto.practice_id());
        if (dto.log_date() != null) m.put("log_date", dto.log_date());
        if (dto.value() != null) m.put("value", dto.value());
        if (dto.note() != null) m.put("note", dto.note());
        return m;
    }

    /*Practicas*/

    public Mono<List<Map<String,Object>>> listPractices(String authBearer) {
        String qp = "select=*&order=created_at.desc";
        return data.selectAuth("practices", qp, authBearer);
    }

    public Mono<Map<String,Object>> createPractice(PracticeDto dto, String authBearer) {
        return data.insertAuth("practices", toRow(dto), authBearer)
                .map(list -> (Map<String,Object>) list.get(0));
    }

    public Mono<Map<String,Object>> updatePractice(String practiceId, PracticeDto dto, String authBearer) {
        String qp = "id=eq." + practiceId;
        return data.patchAuth("practices", qp, toRow(dto), authBearer)
                .map(list -> list.isEmpty() ? Map.of() : (Map<String,Object>) list.get(0));
    }

    public Mono<Integer> deletePractice(String practiceId, String authBearer) {
        return data.deleteAuth("practices", "id=eq." + practiceId, authBearer);
    }

    /* Entradas para practicas */

    public Mono<List<Map<String,Object>>> listEntries(String practiceId, String fromDate, String authBearer) {
        String qp = "select=*&practice_id=eq." + practiceId +
                (fromDate != null ? "&log_date=gte." + fromDate : "") +
                "&order=log_date.desc";
        return data.selectAuth("practice_entries", qp, authBearer);
    }

    public Mono<Map<String,Object>> createEntry(PracticeEntryDto dto, String authBearer) {
        return data.insertAuth("practice_entries", toRow(dto), authBearer)
                .map(list -> (Map<String,Object>) list.get(0));
    }

    public Mono<Map<String,Object>> upsertEntry(String practiceId, String logDate, Double value, String note, String authBearer) {
        Map<String,Object> payload = new HashMap<>();
        payload.put("p_practice_id", practiceId);
        payload.put("p_user_id", null); // trigger seteará auth.uid()
        payload.put("p_log_date", logDate);
        payload.put("p_value", value);
        payload.put("p_note", note);

        var typeRef = new ParameterizedTypeReference<Void>() {};
        return data.callRpc("upsert_practice_entry", payload, authBearer, typeRef)
                .then(getEntry(practiceId, logDate, authBearer));
    }

    private Mono<Map<String,Object>> getEntry(String practiceId, String logDate, String authBearer) {
        String qp = "select=*&practice_id=eq." + practiceId + "&log_date=eq." + logDate;
        return data.selectAuth("practice_entries", qp, authBearer)
                .map(list -> list.isEmpty() ? Map.of() : (Map<String,Object>) list.get(0));
    }

    /* Estadisticas para practicas */

    public Mono<List<Map<String,Object>>> weeklyStats(String authBearer) {
        String qp = "select=practice_id,name,days_per_week,achieved_days_last7,logged_days_last7&order=name.asc";
        return data.selectAuth("practice_weekly_stats", qp, authBearer);
    }

    /* RPC helpers para la autentificacion con Supabase */

    public Mono<Void> markBoolean(String practiceId, String logDate, boolean done, String note, String authBearer) {
        Map<String,Object> payload = Map.of(
                "p_practice_id", practiceId,
                "p_log_date", logDate,
                "p_done", done,
                "p_note", note
        );
        var typeRef = new ParameterizedTypeReference<Void>() {};
        return data.callRpc("mark_practice_boolean", payload, authBearer, typeRef).then();
    }

    public Mono<Void> markQuantity(String practiceId, String logDate, double value, String note, String authBearer) {
        Map<String,Object> payload = Map.of(
                "p_practice_id", practiceId,
                "p_log_date", logDate,
                "p_value", value,
                "p_note", note
        );
        var typeRef = new ParameterizedTypeReference<Void>() {};
        return data.callRpc("mark_practice_quantity", payload, authBearer, typeRef).then();
    }
}
