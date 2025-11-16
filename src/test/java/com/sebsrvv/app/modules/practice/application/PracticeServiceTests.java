package com.sebsrvv.app.modules.practice.application;

import com.sebsrvv.app.modules.practice.domain.Practices;
import com.sebsrvv.app.modules.practice.domain.PracticesRepository;
import com.sebsrvv.app.modules.practice.exception.*;
import com.sebsrvv.app.modules.practice.web.dto.PracticesDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Practices Service - Pruebas Unitarias Mejoradas")
public class PracticeServiceTests {

    @Mock
    private PracticesRepository practicesRepository;

    @InjectMocks
    private PracticesService practicesService;

    UUID id = UUID.randomUUID();
    UUID userId = UUID.fromString("641ef3e1-9d56-4487-8e1e-d89733103ed0");

    // ==================== TESTS DE CREACIÓN ====================

    @Test
    @DisplayName("Crea una práctica con todos los campos válidos")
    public void crearPracticaExitosa() {
        // Arrange
        PracticesDTO request = crearPracticeDTO("quantity", "gte");
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.save(any(Practices.class))).thenReturn(mockPractice);

        // Act
        PracticesDTO respuesta = practicesService.createPractice(request, userId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getName()).isEqualTo(request.getName());
        assertThat(respuesta.getValue_kind()).isEqualTo(request.getValue_kind());

        verify(practicesRepository, times(1)).save(any(Practices.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "texto", "number", ""})
    @DisplayName("Lanza excepción con value_kind inválido")
    public void crearPracticaConValueKindInvalido(String invalidKind) {
        // Arrange
        PracticesDTO request = crearPracticeDTO(invalidKind, "gte");

        // Act & Assert
        assertThatThrownBy(() -> practicesService.createPractice(request, userId))
                .isInstanceOf(PracticeValueKindException.class)
                .hasMessageContaining("value kind");

        verify(practicesRepository, never()).save(any(Practices.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"quantity", "boolean"})
    @DisplayName("Acepta todos los value_kind válidos")
    public void crearPracticaConValueKindValidos(String validKind) {
        // Arrange
        PracticesDTO request = crearPracticeDTO(validKind, "gte");
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.save(any(Practices.class))).thenReturn(mockPractice);

        // Act
        PracticesDTO respuesta = practicesService.createPractice(request, userId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getValue_kind()).isEqualTo(validKind);
        verify(practicesRepository, times(1)).save(any(Practices.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"invalid", "gt", "lt", "neq", ""})
    @DisplayName("Lanza excepción con operador inválido")
    public void crearPracticaConOperadorInvalido(String invalidOperator) {
        // Arrange
        PracticesDTO request = crearPracticeDTO("quantity", invalidOperator);

        // Act & Assert
        assertThatThrownBy(() -> practicesService.createPractice(request, userId))
                .isInstanceOf(PracticeOperatorException.class)
                .hasMessageContaining("operador");

        verify(practicesRepository, never()).save(any(Practices.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"gte", "lte", "eq"})
    @DisplayName("Acepta todos los operadores válidos")
    public void crearPracticaConOperadoresValidos(String validOperator) {
        // Arrange
        PracticesDTO request = crearPracticeDTO("quantity", validOperator);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.save(any(Practices.class))).thenReturn(mockPractice);

        // Act
        PracticesDTO respuesta = practicesService.createPractice(request, userId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getPractice_operator()).isEqualTo(validOperator);
        verify(practicesRepository, times(1)).save(any(Practices.class));
    }

    // ==================== TESTS DE EDICIÓN ====================

    @Test
    @DisplayName("Edita una práctica exitosamente")
    public void editarPracticaExitosa() {
        // Arrange
        String nuevoNombre = "Práctica Actualizada";
        PracticesDTO request = crearPracticeDTO("quantity", "gte");
        request.setName(nuevoNombre);

        Practices existingPractice = crearMockPractice();
        when(practicesRepository.findById(id)).thenReturn(Optional.of(existingPractice));
        when(practicesRepository.save(any(Practices.class))).thenReturn(existingPractice);

        // Act
        PracticesDTO respuesta = practicesService.updatePractice(request, id);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getName()).isEqualTo(nuevoNombre);

        verify(practicesRepository, times(1)).findById(id);
        verify(practicesRepository, times(1)).save(any(Practices.class));
    }

    @Test
    @DisplayName("Lanza excepción al editar práctica inexistente")
    public void editarPracticaInexistente() {
        // Arrange
        PracticesDTO request = crearPracticeDTO("quantity", "gte");
        when(practicesRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> practicesService.updatePractice(request, id))
                .isInstanceOf(NoPracticeException.class)
                .hasMessageContaining(id.toString());

        verify(practicesRepository, times(1)).findById(id);
        verify(practicesRepository, never()).save(any(Practices.class));
    }

    @Test
    @DisplayName("Edición valida value_kind correctamente")
    public void editarPracticaValidaValueKind() {
        // Arrange
        PracticesDTO request = crearPracticeDTO("invalid_kind", "gte");
        Practices existingPractice = crearMockPractice();
        when(practicesRepository.findById(id)).thenReturn(Optional.of(existingPractice));

        // Act & Assert
        assertThatThrownBy(() -> practicesService.updatePractice(request, id))
                .isInstanceOf(PracticeValueKindException.class);

        verify(practicesRepository, times(1)).findById(id);
        verify(practicesRepository, never()).save(any(Practices.class));
    }

    @Test
    @DisplayName("Edición valida operador correctamente")
    public void editarPracticaValidaOperador() {
        // Arrange
        PracticesDTO request = crearPracticeDTO("quantity", "invalid_op");
        Practices existingPractice = crearMockPractice();
        when(practicesRepository.findById(id)).thenReturn(Optional.of(existingPractice));

        // Act & Assert
        assertThatThrownBy(() -> practicesService.updatePractice(request, id))
                .isInstanceOf(PracticeOperatorException.class);

        verify(practicesRepository, times(1)).findById(id);
        verify(practicesRepository, never()).save(any(Practices.class));
    }

    // ==================== TESTS DE ELIMINACIÓN ====================

    @Test
    @DisplayName("Elimina práctica con método soft")
    public void eliminarPracticaSoft() {
        // Arrange
        Practices existingPractice = crearMockPractice();
        existingPractice.setIsActive(true);
        when(practicesRepository.findById(id)).thenReturn(Optional.of(existingPractice));

        // Act
        Boolean resultado = practicesService.deletePractice("soft", id);

        // Assert
        assertThat(resultado).isTrue();
        assertThat(existingPractice.getIsActive()).isFalse();

        verify(practicesRepository, times(1)).findById(id);
        verify(practicesRepository, never()).delete(any(Practices.class));
    }

    @Test
    @DisplayName("Elimina práctica con método hard")
    public void eliminarPracticaHard() {
        // Arrange
        Practices existingPractice = crearMockPractice();
        when(practicesRepository.findById(id)).thenReturn(Optional.of(existingPractice));

        // Act
        Boolean resultado = practicesService.deletePractice("hard", id);

        // Assert
        assertThat(resultado).isTrue();

        verify(practicesRepository, times(1)).findById(id);
        verify(practicesRepository, times(1)).delete(existingPractice);
    }

    @Test
    @DisplayName("Lanza excepción con método de eliminación inválido")
    public void eliminarPracticaMetodoInvalido() {
        // Arrange
        Practices existingPractice = crearMockPractice();
        when(practicesRepository.findById(id)).thenReturn(Optional.of(existingPractice));

        // Act & Assert
        assertThatThrownBy(() -> practicesService.deletePractice("invalid", id))
                .isInstanceOf(NoValidDeleteException.class)
                .hasMessageContaining("soft o hard");

        verify(practicesRepository, times(1)).findById(id);
        verify(practicesRepository, never()).delete(any(Practices.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"SOFT", "HARD", "Soft", "Hard", "SoFt"})
    @DisplayName("Verifica case sensitivity del método de eliminación")
    public void eliminarPracticaCaseSensitivity(String metodo) {
        // Arrange
        Practices existingPractice = crearMockPractice();
        when(practicesRepository.findById(id)).thenReturn(Optional.of(existingPractice));

        // Act & Assert
        assertThatThrownBy(() -> practicesService.deletePractice(metodo, id))
                .isInstanceOf(NoValidDeleteException.class);

        verify(practicesRepository, times(1)).findById(id);
    }

    @Test
    @DisplayName("Lanza excepción al eliminar práctica inexistente")
    public void eliminarPracticaInexistente() {
        // Arrange
        when(practicesRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> practicesService.deletePractice("soft", id))
                .isInstanceOf(NoPracticeException.class)
                .hasMessageContaining(id.toString());

        verify(practicesRepository, times(1)).findById(id);
        verify(practicesRepository, never()).delete(any(Practices.class));
    }

    // ==================== TESTS DE CASOS EXTREMOS ====================

    @Test
    @DisplayName("Maneja valores nulos en campos opcionales")
    public void crearPracticaConCamposNulos() {
        // Arrange
        PracticesDTO request = crearPracticeDTO("quantity", "gte");
        request.setDescription(null);
        request.setIcon(null);
        request.setTarget_unit(null);

        Practices mockPractice = crearMockPractice();
        when(practicesRepository.save(any(Practices.class))).thenReturn(mockPractice);

        // Act
        PracticesDTO respuesta = practicesService.createPractice(request, userId);

        // Assert
        assertThat(respuesta).isNotNull();
        verify(practicesRepository, times(1)).save(any(Practices.class));
    }

    @Test
    @DisplayName("Maneja valores extremos para days_per_week")
    public void crearPracticaConDaysPerWeekExtremos() {
        // Arrange
        PracticesDTO request = crearPracticeDTO("quantity", "gte");
        request.setDays_per_week(7); // Valor máximo válido

        Practices mockPractice = crearMockPractice();
        when(practicesRepository.save(any(Practices.class))).thenReturn(mockPractice);

        // Act
        PracticesDTO respuesta = practicesService.createPractice(request, userId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getDays_per_week()).isEqualTo(7);
        verify(practicesRepository, times(1)).save(any(Practices.class));
    }

    @Test
    @DisplayName("Maneja práctica ya desactivada en soft delete")
    public void softDeletePracticaYaDesactivada() {
        // Arrange
        Practices existingPractice = crearMockPractice();
        existingPractice.setIsActive(false); // Ya desactivada

        when(practicesRepository.findById(id)).thenReturn(Optional.of(existingPractice));

        // Act
        Boolean resultado = practicesService.deletePractice("soft", id);

        // Assert
        assertThat(resultado).isTrue();
        assertThat(existingPractice.getIsActive()).isFalse();
        verify(practicesRepository, times(1)).findById(id);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private PracticesDTO crearPracticeDTO(String valueKind, String operator) {
        PracticesDTO dto = new PracticesDTO();
        dto.setName("Práctica de prueba");
        dto.setDescription("Descripción de prueba");
        dto.setIcon("🏃");
        dto.setValue_kind(valueKind);
        dto.setTarget_value(30.0);
        dto.setTarget_unit("minutos");
        dto.setPractice_operator(operator);
        dto.setDays_per_week(5);
        dto.setIs_active(true);
        return dto;
    }

    private Practices crearMockPractice() {
        Practices practice = new Practices();
        practice.setId(id);
        practice.setUserId(userId);
        practice.setName("Práctica Mock");
        practice.setDescription("Descripción Mock");
        practice.setIcon("🏃");
        practice.setValueKind("quantity");
        practice.setTargetValue(30.0);
        practice.setTargetUnit("minutos");
        practice.setPracticeOperator("gte");
        practice.setDaysPerWeek(5);
        practice.setIsActive(true);
        return practice;
    }
}