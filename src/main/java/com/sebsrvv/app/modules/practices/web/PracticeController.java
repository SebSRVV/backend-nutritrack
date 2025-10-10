// src/main/java/com/sebsrvv/app/practices/PracticeController.java
package com.sebsrvv.app.modules.practices.web;

import com.sebsrvv.app.modules.practices.application.PracticeService;
import com.sebsrvv.app.modules.practices.web.dto.PracticeDto;
import com.sebsrvv.app.modules.practices.web.dto.PracticeEntryDto;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/api/practices", produces = MediaType.APPLICATION_JSON_VALUE)
public class PracticeController {

    private final PracticeService service;

    public PracticeController(PracticeService service) {
        this.service = service;
    }

    private static String bearer(String authHeader) {
        // Acepta "Bearer eyJ..." ya armado o solo el token
        if (authHeader == null || authHeader.isBlank()) return null;
        return authHeader.startsWith("Bearer ") ? authHeader : "Bearer " + authHeader;
    }

    /* ================== PRACTICES ================== */

    @GetMapping
    public Mono<List<Map<String,Object>>> list(@RequestHeader("Authorization") String authorization) {
        return service.listPractices(bearer(authorization));
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String,Object>> create(@RequestHeader("Authorization") String authorization,
                                           @RequestBody PracticeDto dto) {
        return service.createPractice(dto, bearer(authorization));
    }

    @PatchMapping(value="/{practiceId}", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String,Object>> update(@RequestHeader("Authorization") String authorization,
                                           @PathVariable String practiceId,
                                           @RequestBody PracticeDto dto) {
        return service.updatePractice(practiceId, dto, bearer(authorization));
    }

    @DeleteMapping("/{practiceId}")
    public Mono<Map<String,Integer>> delete(@RequestHeader("Authorization") String authorization,
                                            @PathVariable String practiceId) {
        return service.deletePractice(practiceId, bearer(authorization))
                .map(code -> Map.of("status", code));
    }

    /* ================== ENTRIES ================== */

    @GetMapping("/{practiceId}/entries")
    public Mono<List<Map<String,Object>>> listEntries(@RequestHeader("Authorization") String authorization,
                                                      @PathVariable String practiceId,
                                                      @RequestParam(name="from", required = false) String fromDate) {
        return service.listEntries(practiceId, fromDate, bearer(authorization));
    }

    @PostMapping(value="/{practiceId}/entries", consumes = MediaType.APPLICATION_JSON_VALUE)
    public Mono<Map<String,Object>> createEntry(@RequestHeader("Authorization") String authorization,
                                                @PathVariable String practiceId,
                                                @RequestBody PracticeEntryDto dto) {
        // tolera que no manden practice_id en body
        var body = new PracticeEntryDto(dto.id(), practiceId,
                dto.log_date(), dto.value(), dto.note(), dto.achieved());
        return service.createEntry(body, bearer(authorization));
    }

    // Upsert conveniente (evita duplicados por día)
    @PostMapping("/{practiceId}/entries/upsert")
    public Mono<Map<String,Object>> upsertEntry(@RequestHeader("Authorization") String authorization,
                                                @PathVariable String practiceId,
                                                @RequestParam String date,      // YYYY-MM-DD
                                                @RequestParam Double value,     // 1/0 si es booleano
                                                @RequestParam(required=false) String note) {
        return service.upsertEntry(practiceId, date, value, note, bearer(authorization));
    }

    /* ================== STATS ================== */

    @GetMapping("/weekly-stats")
    public Mono<List<Map<String,Object>>> weeklyStats(@RequestHeader("Authorization") String authorization) {
        return service.weeklyStats(bearer(authorization));
    }

    /* ================== RPCs opcionales ================== */

    @PostMapping("/{practiceId}/mark/boolean")
    public Mono<Map<String,String>> markBoolean(@RequestHeader("Authorization") String authorization,
                                                @PathVariable String practiceId,
                                                @RequestParam String date,
                                                @RequestParam boolean done,
                                                @RequestParam(required=false) String note) {
        return service.markBoolean(practiceId, date, done, note, bearer(authorization))
                .thenReturn(Map.of("result","ok"));
    }

    @PostMapping("/{practiceId}/mark/quantity")
    public Mono<Map<String,String>> markQuantity(@RequestHeader("Authorization") String authorization,
                                                 @PathVariable String practiceId,
                                                 @RequestParam String date,
                                                 @RequestParam double value,
                                                 @RequestParam(required=false) String note) {
        return service.markQuantity(practiceId, date, value, note, bearer(authorization))
                .thenReturn(Map.of("result","ok"));
    }
}
