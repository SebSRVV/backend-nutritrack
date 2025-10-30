package com.sebsrvv.app.modules.practice.web;


import com.sebsrvv.app.modules.practice.application.PracticesEntriesService;
import com.sebsrvv.app.modules.practice.application.PracticesService;
import com.sebsrvv.app.modules.practice.web.dto.PracticesEntriesDTO;
import com.sebsrvv.app.modules.practice.web.dto.PracticesEntriesRequest;
import com.sebsrvv.app.modules.practice.web.dto.PracticesRequest;
import org.springframework.beans.factory.annotation.Autowired;
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

    @PostMapping("/crear/{id}")
    public ResponseEntity<?> CrearPractica(@RequestBody PracticesRequest Cuerpo, @PathVariable UUID id){
        practicesService.createPractice(Cuerpo,id);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<?> EditarPractica(@RequestBody PracticesRequest Cuerpo, @PathVariable UUID id){
        practicesService.updatePractice(Cuerpo,id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> EliminarPractica(@PathVariable UUID id){
        practicesService.deletePractice(id);
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
}
