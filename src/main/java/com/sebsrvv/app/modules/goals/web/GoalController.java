// web/GoalController.java
package com.sebsrvv.app.modules.goals.web;

import com.sebsrvv.app.modules.goals.application.GoalService;
import com.sebsrvv.app.modules.goals.web.dto.GoalDto;
import com.sebsrvv.app.modules.goals.web.dto.GoalProgressDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value="/api/goals", produces=MediaType.APPLICATION_JSON_VALUE)
public class GoalController {

    private final GoalService service;
    public GoalController(GoalService service){ this.service = service; }

    private static String bearer(String h){ return (h!=null && h.startsWith("Bearer "))? h : "Bearer "+h; }

    /* Goals */
    @GetMapping public Mono<List<Map<String,Object>>> list(@RequestHeader("Authorization") String a){
        return service.list(bearer(a));
    }

    @PostMapping(consumes=MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String,Object>> create(@RequestHeader("Authorization") String a, @RequestBody GoalDto dto){
        return service.create(dto, bearer(a));
    }

    @PatchMapping(value="/{goalId}", consumes=MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String,Object>> update(@RequestHeader("Authorization") String a, @PathVariable String goalId, @RequestBody GoalDto dto){
        return service.update(goalId, dto, bearer(a));
    }

    @DeleteMapping("/{goalId}")
    public Mono<Map<String,Integer>> delete(@RequestHeader("Authorization") String a, @PathVariable String goalId,
                                            @RequestParam(name="mode", required=false, defaultValue="hard") String mode){
        return service.delete(goalId, "soft".equalsIgnoreCase(mode), bearer(a));
    }

    /* Progress */
    @GetMapping("/{goalId}/progress")
    public Mono<List<Map<String,Object>>> listProgress(@RequestHeader("Authorization") String a,
                                                       @PathVariable String goalId,
                                                       @RequestParam(name="from", required=false) String fromDate){
        return service.listProgress(goalId, fromDate, bearer(a));
    }

    @PostMapping(value="/{goalId}/progress", consumes=MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String,Object>> createProgress(@RequestHeader("Authorization") String a,
                                                   @PathVariable String goalId,
                                                   @RequestBody GoalProgressDto dto){
        var body = new GoalProgressDto(dto.id(), goalId, dto.log_date(), dto.value(), dto.note());
        return service.createProgress(body, bearer(a));
    }

    @PostMapping("/{goalId}/progress/upsert")
    public Mono<Map<String,Object>> upsertProgress(@RequestHeader("Authorization") String a,
                                                   @PathVariable String goalId,
                                                   @RequestParam String date,
                                                   @RequestParam Integer value,  // 0/1
                                                   @RequestParam(required=false) String note){
        return service.upsertProgress(goalId, date, value, note, bearer(a));
    }

    /* Stats */
    @GetMapping("/weekly-stats")
    public Mono<List<Map<String,Object>>> weeklyStats(@RequestHeader("Authorization") String a){
        return service.weeklyStats(bearer(a));
    }

    @GetMapping("/{goalId}/weekly-stats")
    public Mono<List<Map<String,Object>>> weeklyDetail(@RequestHeader("Authorization") String a,
                                                       @PathVariable String goalId,
                                                       @RequestParam(name="weekStart") String weekStart){
        return service.weeklyDetail(goalId, weekStart, bearer(a));
    }
}
