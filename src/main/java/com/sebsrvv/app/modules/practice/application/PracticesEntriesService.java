package com.sebsrvv.app.modules.practice.application;

import com.sebsrvv.app.modules.practice.domain.PracticesEntries;
import com.sebsrvv.app.modules.practice.domain.PracticesEntriesRepository;
import com.sebsrvv.app.modules.practice.domain.PracticesRepository;
import com.sebsrvv.app.modules.practice.exception.NoEntryFoundException;
import com.sebsrvv.app.modules.practice.exception.NoPracticeException;
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

    @Autowired
    private PracticesRepository practicesRepository;

    @Transactional
    public PracticesEntriesDTO create(PracticesEntriesDTO dto, UUID practiceId, UUID userId) {
        PracticesEntries entrada = new PracticesEntries();
        if (practicesRepository.findById(practiceId).isPresent()) {
            entrada.setPracticeId(practiceId);
        } else{
            throw new NoPracticeException(practiceId);
        }

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
                .orElseThrow(() -> new NoEntryFoundException(id));

        entrada.setValue(body.getValue());
        entrada.setNote(body.getNote());
        entrada.setAchieved(body.getAchieved());
        entrada.setLoggedAt(LocalDateTime.now());
        practicesEntriesRepository.save(entrada);
        return body;

    }

    @Transactional
    public void delete(UUID id) {
        PracticesEntries entrada = practicesEntriesRepository.findById(id)
                .orElseThrow(() -> new NoEntryFoundException(id));
        practicesEntriesRepository.delete(entrada);
    }
}
