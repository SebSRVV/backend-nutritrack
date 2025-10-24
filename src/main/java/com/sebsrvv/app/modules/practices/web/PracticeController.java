package com.sebsrvv.app.modules.practices.web;


import com.sebsrvv.app.modules.practices.web.dto.PracticeResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.sebsrvv.app.modules.practices.application.PracticeService;
import com.sebsrvv.app.modules.practices.web.dto.PracticeRequest;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api")
public class PracticeController {

    private final PracticeService practiceService;

    @Autowired
    public PracticeController(PracticeService practiceService) {
        this.practiceService = practiceService;
    }

    @PostMapping("/crearpractica")
    public ResponseEntity<?> create(@RequestBody PracticeRequest practice) {
        PracticeResponse response = practiceService.createPractice(practice);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/editar/{id}")
    public ResponseEntity<?> update(@RequestBody PracticeRequest practice, @PathVariable UUID id) {
        PracticeResponse response = practiceService.updatePractice(practice, id);
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id) {
        practiceService.EliminarPractice(id);
        return ResponseEntity.ok().build();
    }
}


