package com.sebsrvv.app.modules.practice.application;

import com.sebsrvv.app.modules.practice.domain.Practices;
import com.sebsrvv.app.modules.practice.domain.PracticesRepository;
import com.sebsrvv.app.modules.practice.domain.PracticesWeekStats;
import com.sebsrvv.app.modules.practice.domain.PracticesWeekStatsRepository;
import com.sebsrvv.app.modules.practice.exception.NoPracticeException;
import com.sebsrvv.app.modules.practice.web.dto.PracticesWeekStatsRequest;
import com.sebsrvv.app.modules.practice.web.dto.PracticesWeekStatsResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("PracticesWeekStats - Pruebas Unitarias Mejoradas")
public class PracticeWeekStatsServiceTest {

    @Mock
    private PracticesWeekStatsRepository practicesWeekStatsRepository;

    @Mock
    private PracticesRepository practicesRepository;

    @InjectMocks
    private PracticesWeekStatsService practicesWeekStatsService;

    UUID id = UUID.randomUUID();
    UUID practiceId = UUID.randomUUID();
    UUID userId = UUID.randomUUID();

    // ==================== TESTS DE CREACIÓN ====================

    @Test
    @DisplayName("Crea un stat semanal exitosamente")
    public void crearWeekStatExitoso() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Estadística Test", (short) 5, 4L, 6L);
        Practices mockPractice = crearMockPractice();
        PracticesWeekStats mockStats = crearMockWeekStats();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesWeekStatsRepository.save(any(PracticesWeekStats.class))).thenReturn(mockStats);

        // Act
        PracticesWeekStatsResponse respuesta = practicesWeekStatsService.create(request, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getName()).isEqualTo(request.getName());
        assertThat(respuesta.getDaysPerWeek()).isEqualTo(request.getDaysPerWeek());
        assertThat(respuesta.getLoggedDaysLast7()).isEqualTo(request.getLoggedDaysLast7());

        verify(practicesRepository, times(2)).findById(practiceId);
        verify(practicesWeekStatsRepository, times(1)).save(any(PracticesWeekStats.class));
    }

    @Test
    @DisplayName("Verifica que se asigna el userId correctamente al crear")
    public void crearWeekStatAsignaUserId() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 7, 5L, 7L);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));

        ArgumentCaptor<PracticesWeekStats> captor = ArgumentCaptor.forClass(PracticesWeekStats.class);
        when(practicesWeekStatsRepository.save(captor.capture())).thenReturn(new PracticesWeekStats());

        // Act
        practicesWeekStatsService.create(request, practiceId);

        // Assert
        PracticesWeekStats savedStats = captor.getValue();
        assertThat(savedStats.getUserId()).isEqualTo(mockPractice.getUserId());
        assertThat(savedStats.getPracticeId()).isEqualTo(practiceId);
    }

    @Test
    @DisplayName("Verifica que se asignan las fechas actuales al crear")
    public void crearWeekStatAsignaFechasActuales() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 5, 3L, 5L);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));

        ArgumentCaptor<PracticesWeekStats> captor = ArgumentCaptor.forClass(PracticesWeekStats.class);
        when(practicesWeekStatsRepository.save(captor.capture())).thenReturn(new PracticesWeekStats());

        LocalDate today = LocalDate.now();

        // Act
        practicesWeekStatsService.create(request, practiceId);

        // Assert
        PracticesWeekStats savedStats = captor.getValue();
        assertThat(savedStats.getFirstLogInRange()).isEqualTo(today);
        assertThat(savedStats.getLastLogInRange()).isEqualTo(today);
    }

    @Test
    @DisplayName("Lanza excepción cuando la práctica no existe al crear")
    public void crearWeekStatPracticaInexistente() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 5, 3L, 5L);
        when(practicesRepository.findById(practiceId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> practicesWeekStatsService.create(request, practiceId))
                .isInstanceOf(NoPracticeException.class)
                .hasMessageContaining(practiceId.toString());

        verify(practicesRepository, times(1)).findById(practiceId);
        verify(practicesWeekStatsRepository, never()).save(any(PracticesWeekStats.class));
    }

    @ParameterizedTest
    @ValueSource(shorts = {1, 3, 5, 7})
    @DisplayName("Crea stats con diferentes valores de días por semana")
    public void crearWeekStatConDiferentesDayPerWeek(short days) {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", days, 3L, 5L);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));

        ArgumentCaptor<PracticesWeekStats> captor = ArgumentCaptor.forClass(PracticesWeekStats.class);
        when(practicesWeekStatsRepository.save(captor.capture())).thenReturn(new PracticesWeekStats());

        // Act
        practicesWeekStatsService.create(request, practiceId);

        // Assert
        PracticesWeekStats savedStats = captor.getValue();
        assertThat(savedStats.getDaysPerWeek()).isEqualTo(days);
    }

    @Test
    @DisplayName("Crea stat con achieved days igual a 0")
    public void crearWeekStatConAchievedDaysCero() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 7, 0L, 5L);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesWeekStatsRepository.save(any(PracticesWeekStats.class))).thenReturn(new PracticesWeekStats());

        // Act
        PracticesWeekStatsResponse respuesta = practicesWeekStatsService.create(request, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        verify(practicesWeekStatsRepository, times(1)).save(any(PracticesWeekStats.class));
    }

    @Test
    @DisplayName("Crea stat con logged days mayor que días de la semana")
    public void crearWeekStatConLoggedDaysMayorQueSemana() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 5, 6L, 8L);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesWeekStatsRepository.save(any(PracticesWeekStats.class))).thenReturn(new PracticesWeekStats());

        // Act
        PracticesWeekStatsResponse respuesta = practicesWeekStatsService.create(request, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getLoggedDaysLast7()).isEqualTo(8L);
        verify(practicesWeekStatsRepository, times(1)).save(any(PracticesWeekStats.class));
    }

    @Test
    @DisplayName("Crea stat con nombre vacío")
    public void crearWeekStatConNombreVacio() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("", (short) 5, 3L, 5L);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesWeekStatsRepository.save(any(PracticesWeekStats.class))).thenReturn(new PracticesWeekStats());

        // Act
        PracticesWeekStatsResponse respuesta = practicesWeekStatsService.create(request, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        verify(practicesWeekStatsRepository, times(1)).save(any(PracticesWeekStats.class));
    }

    // ==================== TESTS DE EDICIÓN ====================

    @Test
    @DisplayName("Edita un stat semanal exitosamente")
    public void editarWeekStatExitoso() {
        // Arrange
        String nuevoNombre = "Estadística Actualizada";
        Short nuevosDays = 6;
        Long nuevosLogged = 7L;

        PracticesWeekStatsRequest request = crearWeekStatsRequest(nuevoNombre, nuevosDays, 5L, nuevosLogged);
        PracticesWeekStats existingStats = crearMockWeekStats();

        when(practicesWeekStatsRepository.findById(id)).thenReturn(Optional.of(existingStats));
        when(practicesWeekStatsRepository.save(any(PracticesWeekStats.class))).thenReturn(existingStats);

        // Act
        PracticesWeekStatsResponse respuesta = practicesWeekStatsService.edit(request, id);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getName()).isEqualTo(nuevoNombre);
        assertThat(respuesta.getDaysPerWeek()).isEqualTo(nuevosDays);
        assertThat(respuesta.getLoggedDaysLast7()).isEqualTo(nuevosLogged);

        verify(practicesWeekStatsRepository, times(1)).findById(id);
        verify(practicesWeekStatsRepository, times(1)).save(any(PracticesWeekStats.class));
    }

    @Test
    @DisplayName("Verifica que se actualiza lastLogInRange en edición")
    public void editarWeekStatActualizaLastLogInRange() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 5, 3L, 6L);
        PracticesWeekStats existingStats = crearMockWeekStats();
        LocalDate originalLastLog = existingStats.getLastLogInRange();

        when(practicesWeekStatsRepository.findById(id)).thenReturn(Optional.of(existingStats));

        ArgumentCaptor<PracticesWeekStats> captor = ArgumentCaptor.forClass(PracticesWeekStats.class);
        when(practicesWeekStatsRepository.save(captor.capture())).thenReturn(existingStats);

        // Act
        practicesWeekStatsService.edit(request, id);

        // Assert
        PracticesWeekStats updatedStats = captor.getValue();
        assertThat(updatedStats.getLastLogInRange()).isAfterOrEqualTo(originalLastLog);
    }

    @Test
    @DisplayName("Verifica que NO se actualiza firstLogInRange en edición")
    public void editarWeekStatNoActualizaFirstLogInRange() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 5, 3L, 6L);
        PracticesWeekStats existingStats = crearMockWeekStats();
        LocalDate originalFirstLog = LocalDate.now().minusDays(7);
        existingStats.setFirstLogInRange(originalFirstLog);

        when(practicesWeekStatsRepository.findById(id)).thenReturn(Optional.of(existingStats));

        ArgumentCaptor<PracticesWeekStats> captor = ArgumentCaptor.forClass(PracticesWeekStats.class);
        when(practicesWeekStatsRepository.save(captor.capture())).thenReturn(existingStats);

        // Act
        practicesWeekStatsService.edit(request, id);

        // Assert
        PracticesWeekStats updatedStats = captor.getValue();
        assertThat(updatedStats.getFirstLogInRange()).isEqualTo(originalFirstLog);
    }

    @Test
    @DisplayName("Lanza excepción al editar stat inexistente")
    public void editarWeekStatInexistente() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 5, 3L, 5L);
        when(practicesWeekStatsRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> practicesWeekStatsService.edit(request, id))
                .isInstanceOf(NoPracticeException.class)
                .hasMessageContaining(id.toString());

        verify(practicesWeekStatsRepository, times(1)).findById(id);
        verify(practicesWeekStatsRepository, never()).save(any(PracticesWeekStats.class));
    }

    @Test
    @DisplayName("Edita stat reduciendo días por semana")
    public void editarWeekStatReduceDaysPerWeek() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 3, 2L, 3L);
        PracticesWeekStats existingStats = crearMockWeekStats();
        existingStats.setDaysPerWeek((short) 7);

        when(practicesWeekStatsRepository.findById(id)).thenReturn(Optional.of(existingStats));
        when(practicesWeekStatsRepository.save(any(PracticesWeekStats.class))).thenReturn(existingStats);

        // Act
        PracticesWeekStatsResponse respuesta = practicesWeekStatsService.edit(request, id);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getDaysPerWeek()).isEqualTo((short) 3);
        verify(practicesWeekStatsRepository, times(1)).save(any(PracticesWeekStats.class));
    }

    @Test
    @DisplayName("Edita stat cambiando de 0 a varios días logueados")
    public void editarWeekStatDeCeroAVariosLogged() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 5, 4L, 5L);
        PracticesWeekStats existingStats = crearMockWeekStats();
        existingStats.setLoggedDaysLast7(0L);

        when(practicesWeekStatsRepository.findById(id)).thenReturn(Optional.of(existingStats));
        when(practicesWeekStatsRepository.save(any(PracticesWeekStats.class))).thenReturn(existingStats);

        // Act
        PracticesWeekStatsResponse respuesta = practicesWeekStatsService.edit(request, id);

        // Assert
        assertThat(respuesta).isNotNull();
        assertThat(respuesta.getLoggedDaysLast7()).isEqualTo(5L);
        verify(practicesWeekStatsRepository, times(1)).save(any(PracticesWeekStats.class));
    }

    // ==================== TESTS DE ELIMINACIÓN ====================

    @Test
    @DisplayName("Elimina un stat semanal exitosamente")
    public void eliminarWeekStatExitoso() {
        // Arrange
        when(practicesWeekStatsRepository.existsById(id)).thenReturn(true);

        // Act
        boolean resultado = practicesWeekStatsService.delete(id);

        // Assert
        assertThat(resultado).isTrue();
        verify(practicesWeekStatsRepository, times(1)).existsById(id);
        verify(practicesWeekStatsRepository, times(1)).deleteById(id);
    }

    @Test
    @DisplayName("Lanza excepción al eliminar stat inexistente")
    public void eliminarWeekStatInexistente() {
        // Arrange
        when(practicesWeekStatsRepository.existsById(id)).thenReturn(false);

        // Act & Assert
        assertThatThrownBy(() -> practicesWeekStatsService.delete(id))
                .isInstanceOf(NoPracticeException.class)
                .hasMessageContaining(id.toString());

        verify(practicesWeekStatsRepository, times(1)).existsById(id);
        verify(practicesWeekStatsRepository, never()).deleteById(any(UUID.class));
    }

    @Test
    @DisplayName("Verifica que delete llama al método correcto del repositorio")
    public void eliminarWeekStatLlamaMetodoCorrecto() {
        // Arrange
        when(practicesWeekStatsRepository.existsById(id)).thenReturn(true);

        // Act
        practicesWeekStatsService.delete(id);

        // Assert
        ArgumentCaptor<UUID> captor = ArgumentCaptor.forClass(UUID.class);
        verify(practicesWeekStatsRepository).deleteById(captor.capture());

        UUID deletedId = captor.getValue();
        assertThat(deletedId).isEqualTo(id);
    }

    // ==================== TESTS DE MAPPING ====================

    @Test
    @DisplayName("Mapea correctamente una entidad a response")
    public void mappingCorrectamente() {
        // Arrange
        PracticesWeekStats stats = crearMockWeekStats();
        stats.setName("Estadística Original");
        stats.setDaysPerWeek((short) 5);
        stats.setAchievedDaysLast7(4L);
        stats.setLoggedDaysLast7(6L);

        // Act
        PracticesWeekStatsResponse response = practicesWeekStatsService.Mapping(stats);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Estadística Original");
        assertThat(response.getDaysPerWeek()).isEqualTo((short) 5);
        assertThat(response.getLoggedDaysLast7()).isEqualTo(6L);
        assertThat(response.getFirstLogInRange()).isEqualTo(stats.getFirstLogInRange());
        assertThat(response.getLastLogInRange()).isEqualTo(stats.getLastLogInRange());
    }

    @Test
    @DisplayName("Mapping no incluye campos de la entidad (id, practiceId, userId)")
    public void mappingNoIncluyeCamposEntidad() {
        // Arrange
        PracticesWeekStats stats = crearMockWeekStats();

        // Act
        PracticesWeekStatsResponse response = practicesWeekStatsService.Mapping(stats);

        // Assert
        assertThat(response).isNotNull();
        // Verificar que el response tiene solo los campos del DTO
        assertThat(response.getName()).isNotNull();
        assertThat(response.getDaysPerWeek()).isNotNull();
        // El response no debe exponer id, practiceId, userId
    }

    @Test
    @DisplayName("Mapping maneja valores nulos correctamente")
    public void mappingManejaValoresNulos() {
        // Arrange
        PracticesWeekStats stats = new PracticesWeekStats();
        stats.setName(null);
        stats.setDaysPerWeek(null);
        stats.setLoggedDaysLast7(null);
        stats.setFirstLogInRange(null);
        stats.setLastLogInRange(null);

        // Act
        PracticesWeekStatsResponse response = practicesWeekStatsService.Mapping(stats);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getName()).isNull();
        assertThat(response.getDaysPerWeek()).isNull();
        assertThat(response.getLoggedDaysLast7()).isNull();
        assertThat(response.getFirstLogInRange()).isNull();
        assertThat(response.getLastLogInRange()).isNull();
    }

    @Test
    @DisplayName("Mapping con fechas diferentes (primera y última)")
    public void mappingConFechasDiferentes() {
        // Arrange
        LocalDate firstDate = LocalDate.now().minusDays(7);
        LocalDate lastDate = LocalDate.now();

        PracticesWeekStats stats = crearMockWeekStats();
        stats.setFirstLogInRange(firstDate);
        stats.setLastLogInRange(lastDate);

        // Act
        PracticesWeekStatsResponse response = practicesWeekStatsService.Mapping(stats);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getFirstLogInRange()).isEqualTo(firstDate);
        assertThat(response.getLastLogInRange()).isEqualTo(lastDate);
        assertThat(response.getLastLogInRange()).isAfter(response.getFirstLogInRange());
    }

    @Test
    @DisplayName("Mapping duplicado no afecta al objeto original")
    public void mappingDuplicadoNoAfectaOriginal() {
        // Arrange
        PracticesWeekStats stats = crearMockWeekStats();
        String originalName = "Original";
        stats.setName(originalName);

        // Act
        PracticesWeekStatsResponse response1 = practicesWeekStatsService.Mapping(stats);
        PracticesWeekStatsResponse response2 = practicesWeekStatsService.Mapping(stats);

        // Assert
        assertThat(response1.getName()).isEqualTo(originalName);
        assertThat(response2.getName()).isEqualTo(originalName);
        assertThat(stats.getName()).isEqualTo(originalName); // No modificado
    }

    // ==================== TESTS DE CASOS EXTREMOS ====================

    @Test
    @DisplayName("Maneja correctamente achievedDays mayor que loggedDays")
    public void crearWeekStatAchievedMayorQueLogged() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 7, 8L, 5L);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesWeekStatsRepository.save(any(PracticesWeekStats.class))).thenReturn(new PracticesWeekStats());

        // Act
        PracticesWeekStatsResponse respuesta = practicesWeekStatsService.create(request, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        verify(practicesWeekStatsRepository, times(1)).save(any(PracticesWeekStats.class));
    }

    @Test
    @DisplayName("Maneja correctamente daysPerWeek = 0")
    public void crearWeekStatConDaysPerWeekCero() {
        // Arrange
        PracticesWeekStatsRequest request = crearWeekStatsRequest("Test", (short) 0, 0L, 0L);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesWeekStatsRepository.save(any(PracticesWeekStats.class))).thenReturn(new PracticesWeekStats());

        // Act
        PracticesWeekStatsResponse respuesta = practicesWeekStatsService.create(request, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        verify(practicesWeekStatsRepository, times(1)).save(any(PracticesWeekStats.class));
    }

    @Test
    @DisplayName("Maneja nombre muy largo")
    public void crearWeekStatConNombreMuyLargo() {
        // Arrange
        String nombreLargo = "A".repeat(500);
        PracticesWeekStatsRequest request = crearWeekStatsRequest(nombreLargo, (short) 5, 3L, 5L);
        Practices mockPractice = crearMockPractice();

        when(practicesRepository.findById(practiceId)).thenReturn(Optional.of(mockPractice));
        when(practicesWeekStatsRepository.save(any(PracticesWeekStats.class))).thenReturn(new PracticesWeekStats());

        // Act
        PracticesWeekStatsResponse respuesta = practicesWeekStatsService.create(request, practiceId);

        // Assert
        assertThat(respuesta).isNotNull();
        verify(practicesWeekStatsRepository, times(1)).save(any(PracticesWeekStats.class));
    }

    // ==================== MÉTODOS AUXILIARES ====================

    private PracticesWeekStatsRequest crearWeekStatsRequest(String name, Short daysPerWeek,
                                                            Long achievedDays, Long loggedDays) {
        PracticesWeekStatsRequest request = new PracticesWeekStatsRequest();
        request.setName(name);
        request.setDaysPerWeek(daysPerWeek);
        request.setAchievedDaysLast7(achievedDays);
        request.setLoggedDaysLast7(loggedDays);
        return request;
    }

    private Practices crearMockPractice() {
        Practices practice = new Practices();
        practice.setId(practiceId);
        practice.setUserId(userId);
        practice.setName("Práctica Mock");
        practice.setIsActive(true);
        return practice;
    }

    private PracticesWeekStats crearMockWeekStats() {
        PracticesWeekStats stats = new PracticesWeekStats();
        stats.setId(id);
        stats.setPracticeId(practiceId);
        stats.setUserId(userId);
        stats.setName("Estadística Mock");
        stats.setDaysPerWeek((short) 5);
        stats.setAchievedDaysLast7(4L);
        stats.setLoggedDaysLast7(6L);
        stats.setFirstLogInRange(LocalDate.now().minusDays(7));
        stats.setLastLogInRange(LocalDate.now());
        return stats;
    }
}