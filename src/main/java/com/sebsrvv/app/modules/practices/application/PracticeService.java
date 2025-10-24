package com.sebsrvv.app.modules.practices.application;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sebsrvv.app.modules.practices.domain.Practice;
import com.sebsrvv.app.modules.practices.domain.PracticeRepository;
import com.sebsrvv.app.modules.practices.web.dto.PracticeRequest;
import com.sebsrvv.app.modules.practices.web.dto.PracticeResponse;

import java.util.UUID;

@Service
public class PracticeService {

    private final PracticeRepository practiceRepository;

    public PracticeService(PracticeRepository practiceRepository) {
        this.practiceRepository = practiceRepository;
    }
    @Transactional
    public PracticeResponse createPractice(PracticeRequest practiceRequest) {
        try {
            Practice nuevoPractice = new Practice();

            // NO establecer ID manualmente
            nuevoPractice.setName(practiceRequest.getName());
            nuevoPractice.setDescription(practiceRequest.getDescription());
            nuevoPractice.setIcon(practiceRequest.getIcon());
            nuevoPractice.setValueKind(practiceRequest.getValue_kind());
            nuevoPractice.setTargetValue(practiceRequest.getTarget_value());
            nuevoPractice.setTargetUnit(practiceRequest.getTarget_unit());
            nuevoPractice.setOperator(practiceRequest.getOperator()); // Ya viene convertido
            nuevoPractice.setDaysPerWeek(practiceRequest.getDays_per_week());
            nuevoPractice.setIsActive(practiceRequest.getIs_active());

            Practice guardar = practiceRepository.save(nuevoPractice);
            return mapToPracticeResponse(guardar);

        } catch (Exception e) {
            throw new RuntimeException("Error al crear práctica: " + e.getMessage(), e);
        }
    }

    @Transactional
    public PracticeResponse updatePractice(PracticeRequest practiceRequest, UUID id) {
        try {
            Practice pract = practiceRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Practice no encontrado con id: " + id));

            pract.setName(practiceRequest.getName());
            pract.setDescription(practiceRequest.getDescription());
            pract.setIcon(practiceRequest.getIcon());
            pract.setValueKind(practiceRequest.getValue_kind());
            pract.setTargetValue(practiceRequest.getTarget_value());
            pract.setTargetUnit(practiceRequest.getTarget_unit());
            pract.setOperator(practiceRequest.getOperator());
            pract.setDaysPerWeek(practiceRequest.getDays_per_week());
            pract.setIsActive(practiceRequest.getIs_active());

            // No necesitas llamar save() explícitamente por el @Transactional
            // pero por claridad:
            Practice actualizado = practiceRepository.save(pract);
            return mapToPracticeResponse(actualizado);

        } catch (Exception e) {
            throw new RuntimeException("Error al actualizar práctica: " + e.getMessage(), e);
        }
    }

    private PracticeResponse mapToPracticeResponse(Practice practice) {
        PracticeResponse response = new PracticeResponse();
        response.setName(practice.getName());
        response.setDescription(practice.getDescription());
        response.setIcon(practice.getIcon());
        response.setValue_kind(practice.getValueKind());
        response.setTarget_value(practice.getTargetValue());
        response.setTarget_unit(practice.getTargetUnit());
        response.setOperator(practice.getOperator()); // Usa toValue()
        response.setDays_per_week(practice.getDaysPerWeek());
        response.setIs_active(practice.getIsActive());
        return response;
    }

    public Boolean EliminarPractice(UUID id) {
        if(practiceRepository.findById(id).isPresent()) {
            practiceRepository.deleteById(id);
            return true;
        } else {
            return false;
        }
    }
}