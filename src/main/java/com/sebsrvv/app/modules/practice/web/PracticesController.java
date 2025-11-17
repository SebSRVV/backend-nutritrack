package com.sebsrvv.app.modules.practice.web;

import com.sebsrvv.app.modules.auth.domain.ProfileRepository;
import com.sebsrvv.app.modules.practice.application.PracticesEntriesService;
import com.sebsrvv.app.modules.practice.application.PracticesService;
import com.sebsrvv.app.modules.practice.application.PracticesWeekStatsService;
import com.sebsrvv.app.modules.practice.domain.*;
import com.sebsrvv.app.modules.practice.exception.NoPracticeException;
import com.sebsrvv.app.modules.practice.exception.NoUserException;
import com.sebsrvv.app.modules.practice.exception.SuccessResponse;
import com.sebsrvv.app.modules.practice.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/practices")
public class PracticesController {

    @Autowired
    private PracticesService practicesService;
    @Autowired
    private PracticesRepository practicesRepository;
    @Autowired
    private PracticesEntriesService practicesEntriesService;
    @Autowired
    private PracticesWeekStatsService practicesWeekStatsService;
    @Autowired
    private ProfileRepository profileRepository;
    @Autowired
    private PracticesEntriesRepository practicesEntriesRepository;
    @Autowired
    private PracticesWeekStatsRepository practicesWeekStatsRepository;


    @GetMapping("/{userId}")
    public List<Practices> getPractices(@PathVariable UUID userId) {
        if (profileRepository.findById(userId).isEmpty()) {
            throw new NoUserException(userId);
        }
        return practicesRepository.findByUserId(userId);
    }

    @PostMapping("/crear/{id}")
    public ResponseEntity<SuccessResponse> crearPractica(@RequestBody PracticesDTO cuerpo, @PathVariable UUID id) {
        if (profileRepository.findById(id).isEmpty()) {
            throw new NoUserException(id);
        }
        practicesService.createPractice(cuerpo, id);

        SuccessResponse response = new SuccessResponse(
                "PRACTICE_CREATED",
                "La práctica se creó con éxito"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<SuccessResponse> editarPractica(@RequestBody PracticesDTO cuerpo, @PathVariable UUID id) {
        practicesRepository.findById(id).orElseThrow(() -> new NoPracticeException(id));
        practicesService.updatePractice(cuerpo, id);

        SuccessResponse response = new SuccessResponse(
                "PRACTICE_EDITED",
                "La práctica se editó con éxito"
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminar/{metodo}/{id}")
    public ResponseEntity<SuccessResponse> eliminarPractica(@PathVariable String metodo, @PathVariable UUID id) {
        practicesRepository.findById(id).orElseThrow(() -> new NoPracticeException(id));
        practicesService.deletePractice(metodo, id);

        SuccessResponse response;
        if ("soft".equals(metodo)) {
            response = new SuccessResponse(
                    "PRACTICE_DISABLED",
                    "La práctica se desactivó con éxito"
            );
        } else {
            response = new SuccessResponse(
                    "PRACTICE_DELETED",
                    "La práctica se eliminó con éxito"
            );
        }
        return ResponseEntity.ok(response);
    }

    @GetMapping("entrada/{practiceId}")
    public List<PracticesEntries> ObtenerEntradas(@PathVariable UUID practiceId) {
        practicesRepository.findById(practiceId)
                .orElseThrow(() -> new NoPracticeException(practiceId));
        return practicesEntriesRepository.findByPracticeId(practiceId);
    }
    @PostMapping("/crearentrada/{practiceId}")
    public ResponseEntity<SuccessResponse> crearEntrada(@RequestBody PracticesEntriesDTO cuerpo, @PathVariable UUID practiceId) {
        Practices evaluar = practicesRepository.findById(practiceId)
                .orElseThrow(() -> new NoPracticeException(practiceId));

        if (profileRepository.findById(evaluar.getUserId()).isEmpty()) {
            throw new NoUserException(evaluar.getUserId());
        }

        practicesEntriesService.create(cuerpo, practiceId);

        SuccessResponse response = new SuccessResponse(
                "ENTRY_CREATED",
                "La entrada se creó con éxito"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/editarentrada/{id}")
    public ResponseEntity<SuccessResponse> editarEntrada(@RequestBody PracticesEntriesDTO cuerpo, @PathVariable UUID id) {
        practicesEntriesService.update(cuerpo, id);

        SuccessResponse response = new SuccessResponse(
                "ENTRY_EDITED",
                "La entrada se editó con éxito"
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/borrarentrada/{id}")
    public ResponseEntity<SuccessResponse> eliminarEntrada(@PathVariable UUID id) {
        practicesEntriesService.delete(id);

        SuccessResponse response = new SuccessResponse(
                "ENTRY_DELETED",
                "La entrada se eliminó con éxito"
        );
        return ResponseEntity.ok(response);
    }

    @GetMapping("/week/{practiceId}")
    public List<PracticesWeekStats> getWeekStats(@PathVariable UUID practiceId) {
        practicesRepository.findById(practiceId)
                .orElseThrow(() -> new NoPracticeException(practiceId));
        return practicesWeekStatsRepository.findByPracticeId(practiceId);
    }
    @PostMapping("/crearweek/{practiceId}")
    public ResponseEntity<SuccessResponse> crearWeek(@RequestBody PracticesWeekStatsRequest cuerpo, @PathVariable UUID practiceId) {
        practicesWeekStatsService.create(cuerpo, practiceId);

        SuccessResponse response = new SuccessResponse(
                "WEEK_CREATED",
                "El stat se creó con éxito"
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PutMapping("/editarweek/{id}")
    public ResponseEntity<SuccessResponse> editarWeek(@RequestBody PracticesWeekStatsRequest cuerpo, @PathVariable UUID id) {
        practicesWeekStatsService.edit(cuerpo, id);

        SuccessResponse response = new SuccessResponse(
                "WEEK_EDITED",
                "El stat se editó con éxito"
        );
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/borrarweek/{id}")
    public ResponseEntity<SuccessResponse> eliminarWeek(@PathVariable UUID id) {
        practicesWeekStatsService.delete(id);

        SuccessResponse response = new SuccessResponse(
                "WEEK_DELETED",
                "El stat se eliminó con éxito"
        );
        return ResponseEntity.ok(response);
    }
}