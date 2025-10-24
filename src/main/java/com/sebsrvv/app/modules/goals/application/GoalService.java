package com.sebsrvv.app.modules.goals.application;

import com.sebsrvv.app.modules.goals.web.dto.GoalDto;
import com.sebsrvv.app.modules.goals.web.dto.GoalProgressDto;
import com.sebsrvv.app.supabase.SupabaseDataClient;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@Service
public class GoalService {

    private static final String T_GOALS    = "user_goals";
    private static final String T_PROGRESS = "user_goal_progress";

    private final SupabaseDataClient data;

    public GoalService(SupabaseDataClient data) {
        this.data = data;
    }

    // ---------- Validaciones según esquema ----------
    private static void require(boolean cond, String msg) {
        if (!cond) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, msg);
    }

    private static void validateGoal(GoalDto dto, boolean creating) {
        if (creating) {
            require(dto.goal_name() != null && !dto.goal_name().isBlank(), "goal_name is required");
            require(dto.weekly_target() != null, "weekly_target is required");
        }
        if (dto.weekly_target() != null) {
            int w = dto.weekly_target();
            require(w >= 1 && w <= 7, "weekly_target must be in [1..7]");
        }
    }

    private static void validateProgress(GoalProgressDto dto) {
        require(dto.goal_id()!=null && !dto.goal_id().isBlank(), "goal_id is required");
        require(dto.log_date()!=null && !dto.log_date().isBlank(), "log_date is required (YYYY-MM-DD)");
        require(dto.value()!=null && (dto.value()==0 || dto.value()==1), "value must be 0 or 1");
    }

    // ===================== GOALS =====================

    public Mono<List<Map<String,Object>>> list(String bearer) {
        String qp = "select=id,goal_name,description,weekly_target,is_active,category_id,created_at,updated_at"
                + "&order=created_at.desc";
        return data.selectAuth(T_GOALS, qp, bearer);
    }

    public Mono<Map<String,Object>> create(GoalDto dto, String bearer) {
        validateGoal(dto, true);
        return data.insertAuth(T_GOALS, GoalMapper.toRow(dto), bearer)
                .map(rows -> rows.isEmpty() ? Map.of() : rows.get(0));
    }

    public Mono<Map<String,Object>> update(String goalId, GoalDto dto, String bearer) {
        validateGoal(dto, false);
        return data.patchAuth(T_GOALS, "id=eq."+goalId, GoalMapper.toRow(dto), bearer)
                .map(rows -> rows.isEmpty() ? Map.of() : rows.get(0));
    }

    /** Soft delete -> is_active=false (RN-12) */
    public Mono<Map<String,Object>> softDelete(String goalId, String bearer) {
        return data.patchAuth(T_GOALS, "id=eq."+goalId, Map.of("is_active", false), bearer)
                .map(ignored -> Map.of("status", 200));
    }

    /** Hard delete -> devuelve HTTP code de Supabase */
    public Mono<Integer> hardDelete(String goalId, String bearer) {
        return data.deleteAuth(T_GOALS, "id=eq."+goalId, bearer);
    }

    // ================== PROGRESS =====================

    public Mono<List<Map<String,Object>>> listProgress(String goalId, String fromDate, String bearer) {
        StringBuilder qp = new StringBuilder("select=*&goal_id=eq.").append(goalId);
        if (fromDate != null && !fromDate.isBlank()) qp.append("&log_date=gte.").append(fromDate);
        qp.append("&order=log_date.desc");
        return data.selectAuth(T_PROGRESS, qp.toString(), bearer);
    }

    /** Insert puro: si ya existe (mismo día) y tienes unique en BD => 409 */
    public Mono<Map<String,Object>> createProgress(GoalProgressDto dto, String bearer) {
        validateProgress(dto);
        return data.insertAuth(T_PROGRESS, GoalMapper.toRow(dto), bearer)
                .map(rows -> rows.isEmpty() ? Map.of() : rows.get(0));
    }


    public Mono<Map<String,Object>> upsertProgress(String goalId, String date, Integer value, String note, String bearer) {
        if (goalId==null || date==null || value==null || (value!=0 && value!=1))
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "goalId/date/value invalid"));

        var payload = Map.<String,Object>of(
                "goal_id",  goalId,
                "log_date", date,
                "note",     note,
                "value",    value
        );
        return data.callRpcListMap("api_insert_goal_progress", payload, bearer)
                .map(rows -> rows.isEmpty() ? Map.of() : rows.get(0));
    }
}
