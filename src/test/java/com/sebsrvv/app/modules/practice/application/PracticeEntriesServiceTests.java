package com.sebsrvv.app.modules.practice.application;

import com.sebsrvv.app.modules.practice.domain.PracticesEntries;
import com.sebsrvv.app.modules.practice.domain.PracticesEntriesRepository;
import com.sebsrvv.app.modules.practice.domain.PracticesWeekStatsRepository;
import com.sebsrvv.app.modules.practice.web.dto.PracticesEntriesDTO;
import org.junit.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PracticesEntries - Pruebas Unitarias")
public class PracticeEntriesServiceTests {
    @Mock private PracticesEntriesRepository practicesEntriesRepository;

    @InjectMocks private PracticesEntriesService practicesEntriesService;

    UUID id = UUID.randomUUID();
    UUID practiceId = UUID.fromString("11388864-0795-4130-923c-3cc42be36a26");
    UUID userId = UUID.fromString("641ef3e1-9d56-4487-8e1e-d89733103ed0");
    LocalDate logDate = LocalDate.now();
    BigDecimal value = new BigDecimal("100");
    String note = "nota";
    Boolean achieved = false;
    LocalDate loggedAt = LocalDate.now();

    @Test
    @DisplayName("Crea una entrada exitosamente")
    public void createPracticeEntries() {
        PracticesEntriesDTO practicesEntries = new PracticesEntriesDTO();
        //practicesEntries.setPracticeId(practiceId);
        //practicesEntries.setUserId(userId);
        //practicesEntries.setLogDate(logDate);
        practicesEntries.setValue(value);
        practicesEntries.setNote(note);
        practicesEntries.setAchieved(achieved);

        //Act
        PracticesEntriesDTO respuesta = practicesEntriesService.create(practicesEntries,practiceId,userId);

        //Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getLoggedAt()).isEqualTo(practicesEntries.getLoggedAt());
        assertThat(respuesta.getValue()).isEqualTo(value);
        assertThat(respuesta.getNote()).isEqualTo(note);
        assertThat(respuesta.getAchieved()).isEqualTo(achieved);
        assertThat(respuesta.getLoggedAt()).isEqualTo(loggedAt);
    }

    @Test
    @DisplayName("Edita una entrada exitosamente")
    public void editPracticeEntries() {
        PracticesEntriesDTO practicesEntries = new PracticesEntriesDTO();
        //practicesEntries.setPracticeId(practiceId);
        //practicesEntries.setUserId(userId);
        //practicesEntries.setLogDate(logDate);
        practicesEntries.setValue(value);
        practicesEntries.setNote("Prueba");
        practicesEntries.setAchieved(true);

        //Act
        PracticesEntriesDTO respuesta = practicesEntriesService.update(practicesEntries,id);

        //Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getLoggedAt()).isEqualTo(practicesEntries.getLoggedAt());
        assertThat(respuesta.getValue()).isEqualTo(value);
        assertThat(respuesta.getNote()).isEqualTo(note);
        assertThat(respuesta.getAchieved()).isEqualTo(achieved);
        assertThat(respuesta.getLoggedAt()).isEqualTo(loggedAt);
    }

    @Test
    @DisplayName("Borra una entrada")
    public void borraPracticeEntries() {
        //Act
        practicesEntriesService.delete(id);

        //Assert

        assertThat(practicesEntriesRepository.findById(id)).isNull();
        verify(practicesEntriesRepository).deleteById(id);
    }
}
