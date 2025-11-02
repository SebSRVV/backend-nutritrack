package com.sebsrvv.app.modules.practice.application;


import com.sebsrvv.app.modules.practice.domain.Practices;
import com.sebsrvv.app.modules.practice.domain.PracticesRepository;
import com.sebsrvv.app.modules.practice.web.dto.PracticesRequest;
import com.sebsrvv.app.modules.practice.web.dto.PracticesResponse;
import org.hibernate.annotations.SoftDelete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class PracticesService {
    @Autowired
    private PracticesRepository practicesRepository;

    //PracticesService practicesService;
    @Transactional
    public PracticesResponse createPractice(PracticesRequest practicesRequest, UUID id) {
        Practices nuevoPractice = new Practices();
        nuevoPractice.setUserId(id);
        nuevoPractice.setName(practicesRequest.getName());
        nuevoPractice.setDescription(practicesRequest.getDescription());
        nuevoPractice.setIcon(practicesRequest.getIcon());
        nuevoPractice.setValueKind(practicesRequest.getValue_kind());
        nuevoPractice.setTargetValue(practicesRequest.getTarget_value());
        nuevoPractice.setTargetUnit(practicesRequest.getTarget_unit());
        nuevoPractice.setPracticeOperator(practicesRequest.getPractice_operator());
        nuevoPractice.setDaysPerWeek(practicesRequest.getDays_per_week());
        nuevoPractice.setIsActive(practicesRequest.getIs_active());
        Practices guardar = practicesRepository.save(nuevoPractice);
        return MappearRespuesta(guardar);
    }

    @Transactional
    public PracticesResponse updatePractice(PracticesRequest practicesRequest, UUID id) {
        Practices actualizar = practicesRepository.findById(id).orElse(null);
        if (actualizar == null) {
            return null;
        } else{
            actualizar.setName(practicesRequest.getName());
            actualizar.setDescription(practicesRequest.getDescription());
            actualizar.setIcon(practicesRequest.getIcon());
            actualizar.setValueKind(practicesRequest.getValue_kind());
            actualizar.setTargetValue(practicesRequest.getTarget_value());
            actualizar.setTargetUnit(practicesRequest.getTarget_unit());
            actualizar.setPracticeOperator(practicesRequest.getPractice_operator());
            actualizar.setDaysPerWeek(practicesRequest.getDays_per_week());
            actualizar.setIsActive(practicesRequest.getIs_active());
            Practices guardar = practicesRepository.save(actualizar);
            return MappearRespuesta(guardar);
        }
    }


    //@Transactional
    //public list<PracticesResponse>

    //1. Seguir con las APIs
    //2. Crear los exceptions
    //3. Aplicar Softs
    //Aplicar Soft
    //@SoftDelete
    @SoftDelete
    @Transactional
    public Boolean deletePractice(UUID id) {
        if (practicesRepository.existsById(id)) {
            practicesRepository.deleteById(id);
            return true;
        }
        else{
            return false;
        }
    }

    public PracticesResponse MappearRespuesta(Practices practice){
        PracticesResponse Respuesta = new PracticesResponse();
        Respuesta.setName(practice.getName());
        Respuesta.setDescription(practice.getDescription());
        Respuesta.setIcon(practice.getIcon());
        Respuesta.setValue_kind(practice.getValueKind());
        Respuesta.setTarget_value(practice.getTargetValue());
        Respuesta.setPractice_operator(practice.getPracticeOperator());
        Respuesta.setDays_per_week(practice.getDaysPerWeek());
        Respuesta.setIs_active(practice.getIsActive());
        return Respuesta;
    }
}
