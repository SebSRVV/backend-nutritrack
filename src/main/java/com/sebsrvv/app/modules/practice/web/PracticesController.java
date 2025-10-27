package com.sebsrvv.app.modules.practice.web;


import com.sebsrvv.app.modules.practice.application.PracticesService;
import com.sebsrvv.app.modules.practice.web.dto.PracticesRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sebsrvv.app.modules.practice.application.PracticesService;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PracticesController {

    @Autowired
    private PracticesService practicesService;

    @PostMapping("/crearpractica")
    public ResponseEntity<?> CrearPractica(@RequestBody PracticesRequest Cuerpo ){
        practicesService.createPractice(Cuerpo);
        return ResponseEntity.ok().build();
    }

    @PutMapping("/editarpractica/{id}")
    public ResponseEntity<?> EditarPractica(@RequestBody PracticesRequest Cuerpo, @PathVariable UUID id){
        practicesService.updatePractice(Cuerpo,id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/eliminarpractica/{id}")
    public ResponseEntity<?> EliminarPractica(@PathVariable UUID id){
        practicesService.deletePractice(id);
        return ResponseEntity.ok().build();
    }
}
