// src/main/java/com/sebsrvv/app/modules/goals/application/GoalService.java
package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.web.dto.GoalDto;
import com.sebsrvv.app.modules.goals.web.dto.GoalProgressDto;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class GoalService {

    private final SupabaseDataClient data;

    public GoalService(SupabaseDataClient data) {
        this.data = data;
    }

    /* =========================
       Mappers DTO -> row (Map)
       ========================= */
    private static Map<String, Object> toRow(GoalDto dto) {
        var m = new HashMap<String, Object>();
        if (dto.goal_name() != null)     m.put("goal_name", dto.goal_name());
        if (dto.description() != null)   m.put("description", dto.description());
        if (dto.weekly_target() != null) m.put("weekly_target", dto.weekly_target());
        if (dto.is_active() != null)     m.put("is_active", dto.is_active());
        // user_id lo setea trigger con auth.uid()
        return m;
    }

    private static Map<String, Object> toRow(GoalProgressDto dto) {
        var m = new HashMap<String, Object>();
        if (dto.goal_id() != null)   m.put("goal_id", dto.goal_id());
        if (dto.log_date() != null)  m.put("log_date", dto.log_date()); // YYYY-MM-DD
        if (dto.value() != null)     m.put("value", dto.value());       // 0 | 1
        if (dto.note() != null)      m.put("note", dto.note());
        return m;
    }

    /* ==================
       Goals (CRUD/RLS)
       ================== */

    /** Listar metas del usuario autenticado. */
    public Mono<List<Map<String, Object>>> list(String bearer) {
        var qp = "select=*&order=created_at.desc";
        return data.selectAuth("user_goals", qp, bearer);
    }

    /** Crear meta. */
    public Mono<Map<String, Object>> create(GoalDto dto, String bearer) {
        return data.insertAuth("user_goals", toRow(dto), bearer)
                .map(list -> list.isEmpty() ? Map.of() : list.get(0));
    }

    /** Actualizar meta por id. */
    public Mono<Map<String, Object>> update(String goalId, GoalDto dto, String bearer) {
        return data.patchAuth("user_goals", "id=eq." + goalId, toRow(dto), bearer)
                .map(list -> list.isEmpty() ? Map.of() : list.get(0));
    }

    /**
     * Eliminar o desactivar meta.
     * @param soft true => desactivar (is_active=false); false => hard delete
     */
    public Mono<Map<String, Integer>> delete(String goalId, boolean soft, String bearer) {
        if (soft) {
            return data.patchAuth("user_goals", "id=eq." + goalId, Map.of("is_active", false), bearer)
                    .map(list -> Map.of("status", 200));
        }
        return data.deleteAuth("user_goals", "id=eq." + goalId, bearer)
                .map(code -> Map.of("status", code));
    }

    /* =========================
       Progreso (registros día)
       ========================= */

    /** Listar progreso de una meta (desde fecha opcional). */
    public Mono<List<Map<String, Object>>> listProgress(String goalId, String fromDate, String bearer) {
        var qp = new StringBuilder("select=*&goal_id=eq.").append(goalId);
        if (fromDate != null) qp.append("&log_date=gte.").append(fromDate);
        qp.append("&order=log_date.desc");
        return data.selectAuth("user_goal_progress", qp.toString(), bearer);
    }

    /** Crear registro de progreso (insert puro: 409 si ya existe el día). */
    public Mono<Map<String, Object>> createProgress(GoalProgressDto dto, String bearer) {
        return data.insertAuth("user_goal_progress", toRow(dto), bearer)
                .map(list -> list.isEmpty() ? Map.of() : list.get(0));
    }

    /** Upsert de progreso por día (idempotente). */
    public Mono<Map<String, Object>> upsertProgress(String goalId, String date, Integer value, String note, String bearer) {
        var payload = new HashMap<String, Object>();
        payload.put("p_goal_id", goalId);
        payload.put("p_user_id", null);     // lo completa el trigger con auth.uid()
        payload.put("p_log_date", date);    // YYYY-MM-DD
        payload.put("p_value", value);      // 0 | 1
        payload.put("p_note", note);

        return data.callRpcVoid("upsert_goal_progress", payload, bearer)
                .then(getProgress(goalId, date, bearer));
    }

    private Mono<Map<String, Object>> getProgress(String goalId, String date, String bearer) {
        var qp = "select=*&goal_id=eq." + goalId + "&log_date=eq." + date;
        return data.selectAuth("user_goal_progress", qp, bearer)
                .map(list -> list.isEmpty() ? Map.of() : list.get(0));
    }

    /* =========================
       Estadísticas / barra UI
       ========================= */

    /** Resumen semanal (últimos 7 días) para todas las metas del usuario. */
    public Mono<List<Map<String, Object>>> weeklyStats(String bearer) {
        var qp = "select=goal_id,goal_name,weekly_target,is_active,completions_last7,remaining_last7,progress_percent_last7"
                + "&order=goal_name.asc";
        return data.selectAuth("goal_weekly_stats", qp, bearer);
    }

    /** Detalle semanal de una meta (calendario y logs por día). */
    public Mono<List<Map<String, Object>>> weeklyDetail(String goalId, String weekStart, String bearer) {
        var payload = new HashMap<String, Object>();
        payload.put("p_goal_id", goalId);
        payload.put("p_week_start", weekStart); // YYYY-MM-DD
        return data.callRpcListMap("goal_week_detail", payload, bearer);
    }
}
