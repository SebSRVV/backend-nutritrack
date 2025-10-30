package com.sebsrvv.app.modules.practice.application;

import com.sebsrvv.app.modules.practice.domain.PracticesEntries;
import com.sebsrvv.app.modules.practice.domain.PracticesEntriesRepository;
import com.sebsrvv.app.modules.practice.web.dto.PracticesEntriesDTO;
import com.sebsrvv.app.modules.practice.web.dto.PracticesEntriesRequest;
import com.sebsrvv.app.modules.practice.web.dto.PracticesEntriesResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

@Service
public class PracticesEntriesService {
    @Autowired
    private PracticesEntriesRepository practicesEntriesRepository;

    /*@Transactional
    public PracticesEntriesResponse create(PracticesEntriesRequest practicesEntriesRequest, UUID practiceId, UUID userId) {
        PracticesEntries practicesEntries = new PracticesEntries();
        practicesEntries.setId(UUID.randomUUID());
        practicesEntries.setPracticeId(practiceId);
        practicesEntries.setUserId(userId);
        practicesEntries.setLogDate(new Date());
        practicesEntries.setValue(practicesEntriesRequest.getValue());
        practicesEntries.setNote(practicesEntriesRequest.getNote());
        practicesEntries.setAchieved(practicesEntriesRequest.getAchieved());
        practicesEntriesRepository.save(practicesEntries);
        PracticesEntriesResponse respuesta = new PracticesEntriesResponse();
        respuesta.setLogDate(practicesEntries.getLogDate());
        respuesta.setAchieved(practicesEntries.getAchieved());
        respuesta.setNote(practicesEntries.getNote());
        respuesta.setValue(practicesEntries.getValue());
        return respuesta;
    }*/

    @Transactional
    public PracticesEntriesDTO create(PracticesEntriesDTO dto, UUID practiceId, UUID userId) {
        PracticesEntries entrada = new PracticesEntries();
        entrada.setId(UUID.randomUUID());
        entrada.setPracticeId(practiceId);
        entrada.setUserId(userId);
        entrada.setLogDate(LocalDate.now());
        entrada.setValue(dto.getValue());
        entrada.setNote(dto.getNote());
        entrada.setAchieved(dto.getAchieved());
        entrada.setLoggedAt(LocalDateTime.now());
        practicesEntriesRepository.save(entrada);
        return dto;
    }

    @Transactional
    public PracticesEntriesDTO update(PracticesEntriesDTO body, UUID id) {
        PracticesEntries entrada = practicesEntriesRepository.findById(id)
                .orElse(null);

        if (entrada != null) {
            // Actualizar la entidad recuperada
            entrada.setValue(body.getValue());
            entrada.setNote(body.getNote());
            entrada.setAchieved(body.getAchieved());
            entrada.setLoggedAt(LocalDateTime.now());

            // Guardar los cambios
            practicesEntriesRepository.save(entrada);

            return body;
        } else {
            return null;
        }
    }

    @Transactional
    public Boolean  delete(UUID id) {
        if (practicesEntriesRepository.findById(id).isPresent()) {
            practicesEntriesRepository.deleteById(id);
            return true;
        } else{
            return false;
        }
    }
}
