package com.sebsrvv.app.modules.practice.application;

import com.sebsrvv.app.modules.practice.domain.Practices;
import com.sebsrvv.app.modules.practice.domain.PracticesEntries;
import com.sebsrvv.app.modules.practice.domain.PracticesEntriesRepository;
import com.sebsrvv.app.modules.practice.domain.PracticesRepository;
import com.sebsrvv.app.modules.practice.exception.NoEntryFoundException;
import com.sebsrvv.app.modules.practice.exception.NoPracticeException;
import com.sebsrvv.app.modules.practice.web.dto.PracticesEntriesDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PracticesEntries - Pruebas Unitarias Mejoradas")
public class PracticeEntriesServiceTests {

    @Mock
    private PracticesEntriesRepository practicesEntriesRepository;

    @Mock
    private PracticesRepository practicesRepository;

    @InjectMocks
    private PracticesEntriesService practicesEntriesService;

    UUID id = UUID.randomUUID();
    UUID practiceId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    // ==================== TESTS DE CREACIÓN ====================

    @Test
    @DisplayName("Crea una entrada exitosamente")
    public void crearEntradaExitosa() {
        // Arrange
        PracticesEntriesDTO dto = crearEntriesDTO(new BigDecimal("100"), "Nota de prueba", true);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesEntriesRepository.save(any(PracticesEntries.class))).thenReturn(new PracticesEntries());

        // Act
        PracticesEntriesDTO respuesta = practicesEntriesService.create(dto, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getValue()).isEqualTo(new BigDecimal("100"));
        assertThat(respuesta.getNote()).isEqualTo("Nota de prueba");
        assertThat(respuesta.getAchieved()).isTrue();

        verify(practicesRepository, times(2)).findById(practiceId);
        verify(practicesEntriesRepository, times(1)).save(any(PracticesEntries.class));
    }

    @Test
    @DisplayName("Verifica que se asigna el userId correctamente")
    public void crearEntradaAsignaUserId() {
        // Arrange
        PracticesEntriesDTO dto = crearEntriesDTO(new BigDecimal("50"), "Test", false);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));

        ArgumentCaptor<PracticesEntries> captor = ArgumentCaptor.forClass(PracticesEntries.class);
        when(practicesEntriesRepository.save(captor.capture())).thenReturn(new PracticesEntries());

        // Act
        practicesEntriesService.create(dto, practiceId);

        // Assert
        PracticesEntries savedEntry = captor.getValue();
        assertThat(savedEntry.getUserId()).isEqualTo(mockPractice.getUserId());
        assertThat(savedEntry.getPracticeId()).isEqualTo(practiceId);
    }

    @Test
    @DisplayName("Verifica que se asigna la fecha actual")
    public void crearEntradaAsignaFechaActual() {
        // Arrange
        PracticesEntriesDTO dto = crearEntriesDTO(new BigDecimal("75"), "Test", true);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));

        ArgumentCaptor<PracticesEntries> captor = ArgumentCaptor.forClass(PracticesEntries.class);
        when(practicesEntriesRepository.save(captor.capture())).thenReturn(new PracticesEntries());

        LocalDate beforeCreate = LocalDate.now();
        LocalDateTime beforeCreateTime = LocalDateTime.now();

        // Act
        practicesEntriesService.create(dto, practiceId);

        // Assert
        PracticesEntries savedEntry = captor.getValue();
        assertThat(savedEntry.getLogDate()).isEqualTo(beforeCreate);
        assertThat(savedEntry.getLoggedAt()).isAfterOrEqualTo(beforeCreateTime);
    }

    @Test
    @DisplayName("Lanza excepción cuando la práctica no existe")
    public void crearEntradaPracticaInexistente() {
        // Arrange
        PracticesEntriesDTO dto = crearEntriesDTO(new BigDecimal("100"), "Test", true);
        when(practicesRepository.findById(practiceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> practicesEntriesService.create(dto, practiceId))
                .isInstanceOf(NoPracticeException.class)
                .hasMessageContaining(practiceId.toString());

        verify(practicesRepository, times(1)).findById(practiceId);
        verify(practicesEntriesRepository, never()).save(any(PracticesEntries.class));
    }

    @Test
    @DisplayName("Crea entrada con nota vacía")
    public void crearEntradaConNotaVacia() {
        // Arrange
        PracticesEntriesDTO dto = crearEntriesDTO(new BigDecimal("100"), "", false);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesEntriesRepository.save(any(PracticesEntries.class))).thenReturn(new PracticesEntries());

        // Act
        PracticesEntriesDTO respuesta = practicesEntriesService.create(dto, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getNote()).isEmpty();
        verify(practicesEntriesRepository, times(1)).save(any(PracticesEntries.class));
    }

    @Test
    @DisplayName("Crea entrada con nota nula")
    public void crearEntradaConNotaNula() {
        // Arrange
        PracticesEntriesDTO dto = crearEntriesDTO(new BigDecimal("100"), null, false);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesEntriesRepository.save(any(PracticesEntries.class))).thenReturn(new PracticesEntries());

        // Act
        PracticesEntriesDTO respuesta = practicesEntriesService.create(dto, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getNote()).isNull();
        verify(practicesEntriesRepository, times(1)).save(any(PracticesEntries.class));
    }

    @Test
    @DisplayName("Crea entrada con valor cero")
    public void crearEntradaConValorCero() {
        // Arrange
        PracticesEntriesDTO dto = crearEntriesDTO(BigDecimal.ZERO, "Test", false);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesEntriesRepository.save(any(PracticesEntries.class))).thenReturn(new PracticesEntries());

        // Act
        PracticesEntriesDTO respuesta = practicesEntriesService.create(dto, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getValue()).isEqualByComparingTo(BigDecimal.ZERO);
        verify(practicesEntriesRepository, times(1)).save(any(PracticesEntries.class));
    }

    @Test
    @DisplayName("Crea entrada con valor negativo")
    public void crearEntradaConValorNegativo() {
        // Arrange
        PracticesEntriesDTO dto = crearEntriesDTO(new BigDecimal("-50"), "Test", false);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesEntriesRepository.save(any(PracticesEntries.class))).thenReturn(new PracticesEntries());

        // Act
        PracticesEntriesDTO respuesta = practicesEntriesService.create(dto, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getValue()).isEqualByComparingTo(new BigDecimal("-50"));
        verify(practicesEntriesRepository, times(1)).save(any(PracticesEntries.class));
    }

    // ==================== TESTS DE EDICIÓN ====================

    @Test
    @DisplayName("Edita una entrada exitosamente")
    public void editarEntradaExitosa() {
        // Arrange
        String nuevaNota = "Nota actualizada";
        PracticesEntriesDTO dto = crearEntriesDTO(new BigDecimal("200"), nuevaNota, true);

        PracticesEntries existingEntry = crearMockEntry();
        when(practicesEntriesRepository.findById(id)).thenReturn(Optional.of(existingEntry));
        when(practicesEntriesRepository.save(any(PracticesEntries.class))).thenReturn(existingEntry);

        // Act
        PracticesEntriesDTO respuesta = practicesEntriesService.update(dto, id);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getValue()).isEqualTo(new BigDecimal("200"));
        assertThat(respuesta.getNote()).isEqualTo(nuevaNota);
        assertThat(respuesta.getAchieved()).isTrue();

        verify(practicesEntriesRepository, times(1)).findById(id);
        verify(practicesEntriesRepository, times(1)).save(any(PracticesEntries.class));
    }

    @Test
    @DisplayName("Verifica que se actualiza loggedAt en edición")
    public void editarEntradaActualizaLoggedAt() {
        // Arrange
        PracticesEntriesDTO dto = crearEntriesDTO(new BigDecimal("150"), "Test", false);
        PracticesEntries existingEntry = crearMockEntry();
        LocalDateTime originalLoggedAt = existingEntry.getLoggedAt();

        when(practicesEntriesRepository.findById(id)).thenReturn(Optional.of(existingEntry));

        ArgumentCaptor<PracticesEntries> captor = ArgumentCaptor.forClass(PracticesEntries.class);
        when(practicesEntriesRepository.save(captor.capture())).thenReturn(existingEntry);

        // Act
        practicesEntriesService.update(dto, id);

        // Assert
        PracticesEntries updatedEntry = captor.getValue();
        assertThat(updatedEntry.getLoggedAt()).isAfterOrEqualTo(originalLoggedAt);
    }

    @Test
    @DisplayName("Lanza excepción al editar entrada inexistente")
    public void editarEntradaInexistente() {
        // Arrange
        PracticesEntriesDTO dto = crearEntriesDTO(new BigDecimal("100"), "Test", true);
        when(practicesEntriesRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> practicesEntriesService.update(dto, id))
                .isInstanceOf(NoEntryFoundException.class)
                .hasMessageContaining(id.toString());

        verify(practicesEntriesRepository, times(1)).findById(id);
        verify(practicesEntriesRepository, never()).save(any(PracticesEntries.class));
    }

    @Test
    @DisplayName("Edita entrada cambiando achieved de true a false")
    public void editarEntradaCambiaAchievedAFalse() {
        // Arrange
        PracticesEntriesDTO dto = crearEntriesDTO(new BigDecimal("100"), "Test", false);
        PracticesEntries existingEntry = crearMockEntry();
        existingEntry.setAchieved(true);

        when(practicesEntriesRepository.findById(id)).thenReturn(Optional.of(existingEntry));
        when(practicesEntriesRepository.save(any(PracticesEntries.class))).thenReturn(existingEntry);

        // Act
        PracticesEntriesDTO respuesta = practicesEntriesService.update(dto, id);

        // Assert
        assertThat(respuesta.getAchieved()).isFalse();
        verify(practicesEntriesRepository, times(1)).save(any(PracticesEntries.class));
    }

    // ==================== TESTS DE ELIMINACIÓN ====================

    @Test
    @DisplayName("Elimina una entrada exitosamente")
    public void eliminarEntradaExitosa() {
        // Arrange
        PracticesEntries existingEntry = crearMockEntry();
        when(practicesEntriesRepository.findById(id)).thenReturn(Optional.of(existingEntry));

        // Act
        practicesEntriesService.delete(id);

        // Assert
        verify(practicesEntriesRepository, times(1)).findById(id);
        verify(practicesEntriesRepository, times(1)).delete(existingEntry);
    }

    @Test
    @DisplayName("Lanza excepción al eliminar entrada inexistente")
    public void eliminarEntradaInexistente() {
        // Arrange
        when(practicesEntriesRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> practicesEntriesService.delete(id))
                .isInstanceOf(NoEntryFoundException.class)
                .hasMessageContaining(id.toString());

        verify(practicesEntriesRepository, times(1)).findById(id);
        verify(practicesEntriesRepository, never()).delete(any(PracticesEntries.class));
    }

    @Test
    @DisplayName("Verifica que delete llama al método correcto del repositorio")
    public void eliminarEntradaLlamaMetodoCorrecto() {
        // Arrange
        PracticesEntries existingEntry = crearMockEntry();
        when(practicesEntriesRepository.findById(id)).thenReturn(Optional.of(existingEntry));

        // Act
        practicesEntriesService.delete(id);

        // Assert
        ArgumentCaptor<PracticesEntries> captor = ArgumentCaptor.forClass(PracticesEntries.class);
        verify(practicesEntriesRepository).delete(captor.capture());

        PracticesEntries deletedEntry = captor.getValue();
        assertThat(deletedEntry.getId()).isEqualTo(id);
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private PracticesEntriesDTO crearEntriesDTO(BigDecimal value, String note, Boolean achieved) {
        PracticesEntriesDTO dto = new PracticesEntriesDTO();
        dto.setValue(value);
        dto.setNote(note);
        dto.setAchieved(achieved);
        return dto;
    }

    private Practices crearMockPractice() {
        Practices practice = new Practices();
        practice.setId(practiceId);
        practice.setUserId(userId);
        practice.setName("Práctica Mock");
        practice.setIsActive(true);
        return practice;
    }

    private PracticesEntries crearMockEntry() {
        PracticesEntries entry = new PracticesEntries();
        entry.setId(id);
        entry.setPracticeId(practiceId);
        entry.setUserId(userId);
        entry.setLogDate(LocalDate.now());
        entry.setValue(new BigDecimal("100"));
        entry.setNote("Nota original");
        entry.setAchieved(false);
        entry.setLoggedAt(LocalDateTime.now().minusHours(1));
        return entry;
    }
}