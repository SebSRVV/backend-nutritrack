package com.sebsrvv.app.modules.practice.application;


import com.sebsrvv.app.modules.practice.domain.Practices;
import com.sebsrvv.app.modules.practice.domain.PracticesEntries;
import com.sebsrvv.app.modules.practice.domain.PracticesRepository;
import com.sebsrvv.app.modules.practice.exception.*;
import com.sebsrvv.app.modules.practice.web.dto.PracticesDTO;
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
    public PracticesDTO createPractice(PracticesDTO practicesRequest, UUID id) {
        Practices nuevoPractice = new Practices();
        nuevoPractice.setUserId(id);
        nuevoPractice.setName(practicesRequest.getName());
        nuevoPractice.setDescription(practicesRequest.getDescription());
        nuevoPractice.setIcon(practicesRequest.getIcon());

        String kind = practicesRequest.getValue_kind();
        if ("quantity".equals(kind) || "boolean".equals(kind)) {
            nuevoPractice.setValueKind(practicesRequest.getValue_kind());
        } else {
            throw new PracticeValueKindException();
        }
        nuevoPractice.setTargetValue(practicesRequest.getTarget_value());
        nuevoPractice.setTargetUnit(practicesRequest.getTarget_unit());

        String operator = practicesRequest.getPractice_operator();
        if ("gte".equals(operator) || "lte".equals(operator) || "eq".equals(operator)) {
            nuevoPractice.setPracticeOperator(operator);
        } else {
            throw new PracticeOperatorException();
        }
        nuevoPractice.setDaysPerWeek(practicesRequest.getDays_per_week());
        nuevoPractice.setIsActive(practicesRequest.getIs_active());
        Practices guardar = practicesRepository.save(nuevoPractice);
        return practicesRequest;
    }

    @Transactional
    public PracticesDTO updatePractice(PracticesDTO practicesRequest, UUID id) {
        Practices actualizar = practicesRepository.findById(id).orElseThrow(() -> new NoPracticeException(id));

        actualizar.setName(practicesRequest.getName());
        actualizar.setDescription(practicesRequest.getDescription());
        actualizar.setIcon(practicesRequest.getIcon());

        String kind = practicesRequest.getValue_kind();
        if ("quantity".equals(kind) || "boolean".equals(kind)) {
            actualizar.setValueKind(practicesRequest.getValue_kind());
        }
        else {
            throw new PracticeValueKindException();
        }

        actualizar.setTargetValue(practicesRequest.getTarget_value());
        actualizar.setTargetUnit(practicesRequest.getTarget_unit());

        String operator = practicesRequest.getPractice_operator();
        if ("gte".equals(operator) || "lte".equals(operator) || "eq".equals(operator)) {
            actualizar.setPracticeOperator(operator);
        } else {
            throw new PracticeOperatorException();
        }

        actualizar.setDaysPerWeek(practicesRequest.getDays_per_week());
        actualizar.setIsActive(practicesRequest.getIs_active());
        Practices guardar = practicesRepository.save(actualizar);
        return practicesRequest;

    }


    //@Transactional
    //public list<PracticesResponse>

    //1. Seguir con las APIs
    //2. Crear los exceptions
    //3. Aplicar Softs
    //Aplicar Soft
    //@SoftDelete
    //@SoftDelete
    @Transactional
    public Boolean deletePractice(String metodo, UUID id) {
        Practices practica =  practicesRepository.findById(id).orElseThrow(() -> new NoPracticeException(id));
        if("soft".equals(metodo)){
            practica.setIsActive(false);
            return true;
        } else if("hard".equals(metodo)){
            practicesRepository.delete(practica);
            return true;
        } else{
            throw new NoValidDeleteException();
        }
    }

    /*public PracticesResponse MappearRespuesta(Practices practice){
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
    }*/
}
