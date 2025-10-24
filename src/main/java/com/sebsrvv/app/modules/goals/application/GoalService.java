package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.web.dto.GoalDto;
import com.sebsrvv.app.modules.goals.web.dto.GoalProgressDto;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.*;

@Service
public class GoalService {

    private static final String TABLE_GOALS = "user_goals";
    private static final String TABLE_PROGRESS = "user_goal_progress";

    private static final Set<String> ALLOWED_UNITS =
            Set.of("bool","g","ml","kcal","portion","count");
    private static final Set<String> ALLOWED_TYPES =
            Set.of("BOOLEAN","QUANTITATIVE");

    private final SupabaseDataClient data;

    public GoalService(SupabaseDataClient data) {
        this.data = data;
    }

    /* ----------------- validaciones suaves RN-04/RN-03 ----------------- */

    private static void validateGoalSoft(GoalDto dto, boolean creating) {
        if (creating) {
            if (dto.goal_name() == null || dto.goal_name().isBlank())
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "goal_name is required");
            if (dto.weekly_target() == null)
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "weekly_target is required");
        }
        if (dto.weekly_target() != null && (dto.weekly_target() < 1 || dto.weekly_target() > 7))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "weekly_target must be [1..7]");

        if (dto.start_date() != null && dto.end_date() != null) {
            LocalDate s = LocalDate.parse(dto.start_date());
            LocalDate e = LocalDate.parse(dto.end_date());
            if (s.isAfter(e))
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "start_date must be <= end_date");
        }
        if (dto.value_type() != null && !ALLOWED_TYPES.contains(dto.value_type()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "value_type must be " + ALLOWED_TYPES);

        if (dto.unit() != null && !ALLOWED_UNITS.contains(dto.unit()))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "unit must be one of " + ALLOWED_UNITS);
    }

    private Mono<Void> precheckNoDuplicateProgress(String goalId, String date, String bearer) {
        String qp = "select=id&goal_id=eq." + goalId + "&log_date=eq." + date;
        return data.selectAuth(TABLE_PROGRESS, qp, bearer).flatMap(rows -> {
            if (!rows.isEmpty()) {
                return Mono.error(new ResponseStatusException(HttpStatus.CONFLICT,
                        "progress already exists for this date (RN-13)"));
            }
            return Mono.empty();
        });
    }

    private Mono<Void> validateProgressValueAgainstGoal(String goalId, Integer value, String bearer) {
        String qp = "select=id,value_type,unit&id=eq." + goalId;
        return data.selectAuth(TABLE_GOALS, qp, bearer).flatMap(rows -> {
            if (rows.isEmpty()) {
                return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND, "goal not found"));
            }
            Map<String,Object> g = rows.get(0);
            String type = (String) g.get("value_type");
            String unit = (String) g.get("unit");

            // Tu BD guarda value 0/1; validamos que esté en {0,1}
            if (value == null || (value != 0 && value != 1)) {
                return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                        "progress value must be 0 or 1 (current schema)"));
            }
            // Si en el futuro habilitas cuantitativo, aquí ajustas lógica
            return Mono.empty();
        });
    }

    /* ================== GOALS ================== */

    public Mono<List<Map<String, Object>>> list(String bearer) {
        String qp = "select=id,goal_name,description,weekly_target,is_active,category_id,"
                + "created_at,updated_at,value_type,unit,start_date,end_date,target_value"
                + "&order=created_at.desc";
        return data.selectAuth(TABLE_GOALS, qp, bearer);
    }

    public Mono<Map<String, Object>> create(GoalDto dto, String bearer) {
        validateGoalSoft(dto, true);
        Map<String,Object> payload = GoalMapper.toRow(dto);
        return data.insertAuth(TABLE_GOALS, payload, bearer)
                .map(list -> list.isEmpty() ? Map.of() : list.get(0));
    }

    public Mono<Map<String, Object>> update(String goalId, GoalDto dto, String bearer) {
        validateGoalSoft(dto, false);
        Map<String,Object> patch = GoalMapper.toRow(dto);
        return data.patchAuth(TABLE_GOALS, "id=eq." + goalId, patch, bearer)
                .map(list -> list.isEmpty() ? Map.of() : list.get(0));
    }

    public Mono<Map<String, Object>> softDelete(String goalId, String bearer) {
        Map<String,Object> patch = Map.of("is_active", Boolean.FALSE);
        return data.patchAuth(TABLE_GOALS, "id=eq." + goalId, patch, bearer)
                .map(ignored -> Map.of("status", 200)); // RN-12
    }

    public Mono<Map<String,Integer>> delete(String goalId, boolean soft, String bearer) {
        if (soft) {
            return softDelete(goalId, bearer).map(x -> Map.of("status", 200));
        }
        return data.deleteAuth(TABLE_GOALS, "id=eq." + goalId, bearer)
                .map(code -> Map.of("status", code));
    }

    /* ============== PROGRESS (user_goal_progress) ============== */

    public Mono<List<Map<String, Object>>> listProgress(String goalId, String fromDate, String bearer) {
        StringBuilder qp = new StringBuilder("select=*&goal_id=eq.").append(goalId);
        if (fromDate != null) qp.append("&log_date=gte.").append(fromDate);
        qp.append("&order=log_date.desc");
        return data.selectAuth(TABLE_PROGRESS, qp.toString(), bearer);
    }

    /** Insert puro: valida RN-13 por pre-check y RN-04/RN-03 contra goal; respeta schema actual (value 0/1). */
    public Mono<Map<String, Object>> createProgress(GoalProgressDto dto, String bearer) {
        if (dto.goal_id() == null || dto.log_date() == null || dto.value() == null) {
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "goal_id, log_date, value are required"));
        }
        return precheckNoDuplicateProgress(dto.goal_id(), dto.log_date(), bearer)
                .then(validateProgressValueAgainstGoal(dto.goal_id(), dto.value(), bearer))
                .then(data.insertAuth(TABLE_PROGRESS, Map.of(
                        "goal_id", dto.goal_id(),
                        "log_date", dto.log_date(),
                        "value", dto.value(),
                        "note", dto.note()
                ), bearer))
                .map(list -> list.isEmpty() ? Map.of() : list.get(0));
    }

    /** Upsert idempotente vía RPC existente (firma con p_*). Si no tienes el RPC, puedes omitir este método. */
    public Mono<Map<String, Object>> upsertProgress(String goalId, String date, Integer value, String note, String bearer) {
        Map<String,Object> payload = new HashMap<>();
        payload.put("p_goal_id",  goalId);
        payload.put("p_log_date", date);
        payload.put("p_value",    value);
        payload.put("p_note",     note);
        return validateProgressValueAgainstGoal(goalId, value, bearer)
                .then(data.callRpcVoid("api_insert_goal_progress", payload, bearer))
                .then(getProgress(goalId, date, bearer));
    }

    private Mono<Map<String, Object>> getProgress(String goalId, String date, String bearer) {
        String qp = "select=*&goal_id=eq." + goalId + "&log_date=eq." + date;
        return data.selectAuth(TABLE_PROGRESS, qp, bearer)
                .map(list -> list.isEmpty() ? Map.of() : list.get(0));
    }

    /* ============== STATS ============== */

    public Mono<List<Map<String, Object>>> weeklyStats(String bearer) {
        // Usa la vista nueva que ya creaste
        String qp = "select=goal_id,goal_name,weekly_target,is_active,"
                + "completions_last7,remaining_last7,progress_percent_last7"
                + "&order=goal_name.asc";
        return data.selectAuth("goal_weekly_stats_v2", qp, bearer);
    }
}
