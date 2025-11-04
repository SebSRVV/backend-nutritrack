package com.sebsrvv.app.modules.practice.web;


import com.sebsrvv.app.modules.practice.application.PracticesEntriesService;
import com.sebsrvv.app.modules.practice.application.PracticesService;
import com.sebsrvv.app.modules.practice.application.PracticesWeekStatsService;
import com.sebsrvv.app.modules.practice.exception.PracticeException;
import com.sebsrvv.app.modules.practice.web.dto.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sebsrvv.app.modules.practice.application.PracticesService;

import java.util.UUID;

@RestController
@RequestMapping("/api/practices")
public class PracticesController {

    @Autowired
    private PracticesService practicesService;

    @Autowired
    private PracticesEntriesService practicesEntriesService;
    @Autowired
    private PracticesWeekStatsService practicesWeekStatsService;

    @PostMapping("/crear/{id}")
    public ResponseEntity<?> CrearPractica(@RequestBody PracticesDTO Cuerpo, @PathVariable UUID id){
        practicesService.createPractice(Cuerpo,id);
        return ResponseEntity.status(HttpStatus.CREATED).body(Cuerpo);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<?> EditarPractica(@RequestBody PracticesDTO Cuerpo, @PathVariable UUID id){
        return ResponseEntity.ok(practicesService.updatePractice(Cuerpo,id));
    }

    //hard o soft
    @DeleteMapping("/eliminar/{metodo}/{id}")
    public ResponseEntity<?> EliminarPractica(@PathVariable String metodo, @PathVariable UUID id){
        practicesService.deletePractice(metodo,id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("crearentrada/{practiceId}/{userId}")
    public ResponseEntity<?> CrearEntrada(@RequestBody PracticesEntriesDTO Cuerpo, @PathVariable UUID practiceId, @PathVariable UUID userId){
        practicesEntriesService.create(Cuerpo,practiceId,userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/editarentrada/{id}")
    public ResponseEntity<?> EditarEntrada(@RequestBody PracticesEntriesDTO Cuerpo, @PathVariable UUID id){
        practicesEntriesService.update(Cuerpo,id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("borrarentrada/{id}")
    public ResponseEntity<?> EliminarEntrada(@PathVariable UUID id){
        practicesEntriesService.delete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("crearweek/{practiceId}/{userId}")
    public ResponseEntity<?> CrearWeek(@RequestBody PracticesWeekStatsRequest Cuerpo, @PathVariable UUID practiceId, @PathVariable UUID userId){
        practicesWeekStatsService.create(Cuerpo,practiceId,userId);
        return ResponseEntity.ok().build();
    }

    @PutMapping("editarweek/{id}")
    public ResponseEntity<?> EditarWeek(@RequestBody PracticesWeekStatsRequest Cuerpo, @PathVariable UUID id){
        practicesWeekStatsService.edit(Cuerpo,id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("borrarweek/{id}")
    public ResponseEntity<?> EliminarWeek(@PathVariable UUID id){
        practicesWeekStatsService.delete(id);
        return ResponseEntity.ok().build();
    }
}
