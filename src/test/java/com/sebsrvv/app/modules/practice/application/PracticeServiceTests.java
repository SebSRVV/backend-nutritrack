package com.sebsrvv.app.modules.practice.application;

import com.sebsrvv.app.modules.practice.domain.Practices;
import com.sebsrvv.app.modules.practice.domain.PracticesRepository;
import com.sebsrvv.app.modules.practice.web.dto.PracticesDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Practices - Pruebas Unitarias")
public class PracticeServiceTests {

    @Mock
    private PracticesRepository practicesRepository;

    @InjectMocks
    private PracticesService practicesService;

    UUID id = UUID.randomUUID();
    UUID userId = UUID.fromString("641ef3e1-9d56-4487-8e1e-d89733103ed0");
    String practicename = "practicename";
    String description = "description";
    String icon = "🧘‍♀️";
    Double target_value = 20.0;
    String target_unit = "minutos";
    String operator = "lte";
    Integer days_per_week = 7;
    Boolean is_active = true;
    String value_kind = "quantity";

    @Test
    @DisplayName("Crea una practica exitosamente")
    public void CrearPractica() {
        // Arrange
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

        Practices mockPractice = new Practices();
        mockPractice.setId(id);
        mockPractice.setUserId(userId);
        mockPractice.setName(practicename);
        mockPractice.setDescription(description);
        mockPractice.setIcon(icon);
        mockPractice.setValueKind(value_kind);
        mockPractice.setTargetValue(target_value);
        mockPractice.setTargetUnit(target_unit);
        mockPractice.setPracticeOperator(operator);
        mockPractice.setDaysPerWeek(days_per_week);
        mockPractice.setIsActive(is_active);

        when(practicesRepository.save(any(Practices.class))).thenReturn(mockPractice);

        // Act
        PracticesDTO respuesta = practicesService.createPractice(request, userId);

        // Assert
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

        // Verificar
        verify(practicesRepository, times(1)).save(any(Practices.class));
        verify(practicesRepository, never()).findById(any(UUID.class));
    }

    @Test
    @DisplayName("Edita una practica exitosamente")
    public void EditarPractica() {
        // Arrange
        String nuevoNombre = "Lol";
        String nuevaDescripcion = "Descripcion de prueba";

        PracticesDTO request = new PracticesDTO();
        request.setName(nuevoNombre);
        request.setDescription(nuevaDescripcion);
        request.setIcon(icon);
        request.setTarget_value(target_value);
        request.setTarget_unit(target_unit);
        request.setPractice_operator(operator);
        request.setDays_per_week(days_per_week);
        request.setIs_active(is_active);
        request.setValue_kind(value_kind);

        Practices existingPractice = new Practices();
        existingPractice.setId(id);
        existingPractice.setUserId(userId);
        existingPractice.setName(practicename);
        existingPractice.setDescription(description);
        existingPractice.setIcon(icon);
        existingPractice.setValueKind(value_kind);
        existingPractice.setTargetValue(target_value);
        existingPractice.setTargetUnit(target_unit);
        existingPractice.setPracticeOperator(operator);
        existingPractice.setDaysPerWeek(days_per_week);
        existingPractice.setIsActive(is_active);

        when(practicesRepository.findById(id)).thenReturn(Optional.of(existingPractice));
        when(practicesRepository.save(any(Practices.class))).thenReturn(existingPractice);

        // Act
        PracticesDTO respuesta = practicesService.updatePractice(request, id);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getName()).isEqualTo(nuevoNombre);
        assertThat(respuesta.getDescription()).isEqualTo(nuevaDescripcion);
        assertThat(respuesta.getIcon()).isEqualTo(icon);
        assertThat(respuesta.getTarget_value()).isEqualTo(target_value);
        assertThat(respuesta.getTarget_unit()).isEqualTo(target_unit);
        assertThat(respuesta.getPractice_operator()).isEqualTo(operator);
        assertThat(respuesta.getDays_per_week()).isEqualTo(days_per_week);
        assertThat(respuesta.getIs_active()).isEqualTo(is_active);
        assertThat(respuesta.getValue_kind()).isEqualTo(value_kind);

        //Verificar
        verify(practicesRepository, times(1)).findById(id);
        verify(practicesRepository, times(1)).save(any(Practices.class));
    }

    @Test
    @DisplayName("Borra una practica exitosamente (hard delete)")
    public void BorrarPracticaHard() {
        // Arrange
        Practices existingPractice = new Practices();
        existingPractice.setId(id);
        existingPractice.setUserId(userId);
        existingPractice.setName(practicename);
        existingPractice.setIsActive(true);

        when(practicesRepository.findById(id)).thenReturn(Optional.of(existingPractice));

        // Act
        Boolean resultado = practicesService.deletePractice("hard", id);

        // Assert
        assertThat(resultado).isTrue();
        verify(practicesRepository, times(1)).findById(id);
        verify(practicesRepository, times(1)).delete(any(Practices.class));
    }

    @Test
    @DisplayName("Borra una practica exitosamente (soft delete)")
    public void BorrarPracticaSoft() {
        // Arrange
        Practices existingPractice = new Practices();
        existingPractice.setId(id);
        existingPractice.setUserId(userId);
        existingPractice.setName(practicename);
        existingPractice.setIsActive(true);

        when(practicesRepository.findById(id)).thenReturn(Optional.of(existingPractice));

        // Act
        Boolean resultado = practicesService.deletePractice("soft", id);

        // Assert
        assertThat(resultado).isTrue();
        assertThat(existingPractice.getIsActive()).isFalse(); // Verificar que se desactivó
        verify(practicesRepository, times(1)).findById(id);
        verify(practicesRepository, never()).delete(any(Practices.class)); // NO debe llamar a delete
    }
}