package com.sebsrvv.app.modules.practice.application;


import com.sebsrvv.app.modules.practice.domain.Practices;
import com.sebsrvv.app.modules.practice.domain.PracticesEntriesRepository;
import com.sebsrvv.app.modules.practice.domain.PracticesRepository;
import com.sebsrvv.app.modules.practice.domain.PracticesWeekStatsRepository;
import com.sebsrvv.app.modules.practice.web.dto.PracticesDTO;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;



@ExtendWith(MockitoExtension.class)
@DisplayName("Practicas - Pruebas Unitarias")
public class PracticeServiceTests {
    @Mock private PracticesRepository practicesRepository;


    @InjectMocks private PracticesService practicesService;


    UUID id = UUID.randomUUID();
    UUID userId = UUID.fromString("641ef3e1-9d56-4487-8e1e-d89733103ed0");
    String practicename = "practicename";
    String description = "description";
    String icon = "\uD83E\uDDD8\u200D♀\uFE0F";
    Double target_value = 20.0;
    String target_unit = "minutos";
    String operator = "lte";
    Integer days_per_week = 7;
    Boolean is_active = true;
    String value_kind = "quantity";

    @Test
    @DisplayName("Crea una practica exitosamente")
    public void CrearPractica() {


        PracticesDTO request = new PracticesDTO();
        request.setName(practicename);
        request.setDescription(description);
        request.setIcon(icon);
        request.setTarget_value(target_value);
        request.setTarget_unit(target_unit);
        request.setPractice_operator(operator);
        request.setDays_per_week(days_per_week);
        request.setIs_active(is_active);
        request.setValue_kind(value_kind);

        //Act
        PracticesDTO respuesta = practicesService.createPractice(request, userId);

        //Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getName()).isEqualTo(practicename);
        assertThat(respuesta.getDescription()).isEqualTo(description);
        assertThat(respuesta.getIcon()).isEqualTo(icon);
        assertThat(respuesta.getTarget_value()).isEqualTo(target_value);
        assertThat(respuesta.getTarget_unit()).isEqualTo(target_unit);
        assertThat(respuesta.getPractice_operator()).isEqualTo(operator);
        assertThat(respuesta.getDays_per_week()).isEqualTo(days_per_week);
        assertThat(respuesta.getIs_active()).isEqualTo(is_active);
        assertThat(respuesta.getValue_kind()).isEqualTo(value_kind);

        verify(practicesRepository).findById(id);
        verify(practicesRepository).save(any(Practices.class));
    }

    @Test
    @DisplayName("Edita una practica exitosamente")
    public void EditarPractica() {
        PracticesDTO request = new PracticesDTO();
        request.setName("Lol");
        request.setDescription("Se edita la descripcion");
        request.setIcon(icon);
        request.setTarget_value(target_value);
        request.setTarget_unit(target_unit);
        request.setPractice_operator(operator);
        request.setDays_per_week(days_per_week);
        request.setIs_active(is_active);
        request.setValue_kind(value_kind);

        //Act
        PracticesDTO respuesta = practicesService.updatePractice(request, id);
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getName()).isEqualTo(practicename);
        assertThat(respuesta.getDescription()).isEqualTo(description);
        assertThat(respuesta.getIcon()).isEqualTo(icon);
        assertThat(respuesta.getTarget_value()).isEqualTo(target_value);
        assertThat(respuesta.getTarget_unit()).isEqualTo(target_unit);
        assertThat(respuesta.getPractice_operator()).isEqualTo(operator);
        assertThat(respuesta.getDays_per_week()).isEqualTo(days_per_week);
        assertThat(respuesta.getIs_active()).isEqualTo(is_active);
        assertThat(respuesta.getValue_kind()).isEqualTo(value_kind);

        verify(practicesRepository).findById(id);
        verify(practicesRepository).save(any(Practices.class));
    }

    @Test
    @DisplayName("Borra una practica exitosamente")
    public void BorrarPractica() {

        //Act
        //practicesService.deletePractice("hard",id);

        assertThat(practicesService.deletePractice("hard",id)).isEqualTo(true);
        verify(practicesRepository).deleteById(id);

    }

}
