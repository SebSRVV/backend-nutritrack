package com.sebsrvv.app.modules.practice.application;

import com.sebsrvv.app.modules.practice.domain.Practices;
import com.sebsrvv.app.modules.practice.domain.PracticesEntries;
import com.sebsrvv.app.modules.practice.domain.PracticesEntriesRepository;
import com.sebsrvv.app.modules.practice.domain.PracticesRepository;
import com.sebsrvv.app.modules.practice.web.dto.PracticesEntriesDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PracticesEntries - Pruebas Unitarias")
public class PracticeEntriesServiceTests {

    @Mock
    private PracticesEntriesRepository practicesEntriesRepository;

    @Mock
    private PracticesRepository practicesRepository;  // ✅ Faltaba este mock

    @InjectMocks
    private PracticesEntriesService practicesEntriesService;

    UUID id = UUID.randomUUID();
    UUID practiceId = UUID.fromString("11388864-0795-4130-923c-3cc42be36a26");
    UUID userId = UUID.fromString("641ef3e1-9d56-4487-8e1e-d89733103ed0");
    LocalDate logDate = LocalDate.now();
    BigDecimal value = new BigDecimal("100");
    String note = "nota";
    Boolean achieved = false;

    @Test
    @DisplayName("Crea una entrada exitosamente")
    public void createPracticeEntries() {
        // Arrange
        PracticesEntriesDTO practicesEntries = new PracticesEntriesDTO();
        practicesEntries.setValue(value);
        practicesEntries.setNote(note);
        practicesEntries.setAchieved(achieved);

        Practices mockPractice = new Practices();
        mockPractice.setId(practiceId);
        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));

        PracticesEntries mockEntry = new PracticesEntries();
        mockEntry.setId(id);
        mockEntry.setPracticeId(practiceId);
        mockEntry.setUserId(userId);
        mockEntry.setLogDate(LocalDate.now());
        mockEntry.setValue(value);
        mockEntry.setNote(note);
        mockEntry.setAchieved(achieved);
        mockEntry.setLoggedAt(LocalDateTime.now());

        when(practicesEntriesRepository.save(any(PracticesEntries.class))).thenReturn(mockEntry);

        // Act
        PracticesEntriesDTO respuesta = practicesEntriesService.create(practicesEntries, practiceId, userId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getValue()).isEqualTo(value);
        assertThat(respuesta.getNote()).isEqualTo(note);
        assertThat(respuesta.getAchieved()).isEqualTo(achieved);

        verify(practicesRepository, times(1)).findById(practiceId);
        verify(practicesEntriesRepository, times(1)).save(any(PracticesEntries.class));
    }

    @Test
    @DisplayName("Edita una entrada exitosamente")
    public void editPracticeEntries() {
        // Arrange
        String nuevaNota = "Prueba";
        Boolean nuevoAchieved = true;

        PracticesEntriesDTO practicesEntries = new PracticesEntriesDTO();
        practicesEntries.setValue(value);
        practicesEntries.setNote(nuevaNota);
        practicesEntries.setAchieved(nuevoAchieved);

        PracticesEntries existingEntry = new PracticesEntries();
        existingEntry.setId(id);
        existingEntry.setPracticeId(practiceId);
        existingEntry.setUserId(userId);
        existingEntry.setLogDate(LocalDate.now());
        existingEntry.setValue(new BigDecimal("50"));
        existingEntry.setNote("Nota original");
        existingEntry.setAchieved(false);
        existingEntry.setLoggedAt(LocalDateTime.now());

        when(practicesEntriesRepository.findById(id)).thenReturn(Optional.of(existingEntry));
        when(practicesEntriesRepository.save(any(PracticesEntries.class))).thenReturn(existingEntry);

        // Act
        PracticesEntriesDTO respuesta = practicesEntriesService.update(practicesEntries, id);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getValue()).isEqualTo(value);
        assertThat(respuesta.getNote()).isEqualTo(nuevaNota);  // ✅ Verificar el nuevo valor
        assertThat(respuesta.getAchieved()).isEqualTo(nuevoAchieved);  // ✅ Verificar el nuevo valor

        verify(practicesEntriesRepository, times(1)).findById(id);
        verify(practicesEntriesRepository, times(1)).save(any(PracticesEntries.class));
    }

    @Test
    @DisplayName("Borra una entrada exitosamente")
    public void borraPracticeEntries() {
        // Arrange
        PracticesEntries existingEntry = new PracticesEntries();
        existingEntry.setId(id);
        existingEntry.setPracticeId(practiceId);
        existingEntry.setUserId(userId);
        existingEntry.setLogDate(LocalDate.now());
        existingEntry.setValue(value);
        existingEntry.setNote(note);
        existingEntry.setAchieved(achieved);
        existingEntry.setLoggedAt(LocalDateTime.now());

        when(practicesEntriesRepository.findById(id)).thenReturn(Optional.of(existingEntry));

        // Act
        practicesEntriesService.delete(id);

        // Assert
        verify(practicesEntriesRepository, times(1)).findById(id);
        verify(practicesEntriesRepository, times(1)).delete(any(PracticesEntries.class));
    }
}