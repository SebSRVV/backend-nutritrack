package com.sebsrvv.app.modules.auth;

import com.sebsrvv.app.modules.auth.domain.ProfileRepository;
import com.sebsrvv.app.modules.auth.domain.UserProfile;
import com.sebsrvv.app.modules.auth.infra.SupabaseAuthClient;
import com.sebsrvv.app.modules.auth.web.dto.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.oauth2.jwt.Jwt;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("AuthService - Pruebas Unitarias")
class AuthTest {

    @Mock private ProfileRepository profileRepository;
    @Mock private SupabaseAuthClient supabaseAuthClient;

    @InjectMocks private AuthService authService;

    private Map<String, Object> tokenResponse(String userId, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("id", userId);
        user.put("email", email);

        Map<String, Object> resp = new HashMap<>();
        resp.put("access_token", "at");
        resp.put("refresh_token", "rt");
        resp.put("token_type", "bearer");
        resp.put("expires_in", 3600);
        resp.put("user", user);
        return resp;
    }

    private Jwt buildJwt(String userId, String email) {
        return Jwt.withTokenValue("token")
                .header("alg", "none")
                .subject(userId)
                .claim("email", email)
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();
    }

    @Test
    @DisplayName("Debe hacer login exitosamente y crear perfil si no existe")
    void login_ValidCredentials_CreatesProfileAndReturnsToken() {
        // Arrange
        String id = UUID.randomUUID().toString();
        String email = "john@doe.com";

        when(supabaseAuthClient.login(email, "Secret123!"))
                .thenReturn(tokenResponse(id, email));
        when(profileRepository.findById(UUID.fromString(id))).thenReturn(Optional.empty());
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        TokenResponse response = authService.login(new LoginRequest(email, "Secret123!"));

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.access_token()).isEqualTo("at");
        assertThat(response.refresh_token()).isEqualTo("rt");
        assertThat(response.token_type()).isEqualTo("bearer");
        assertThat(response.expires_in()).isEqualTo(3600L);
        assertThat(response.user().get("email")).isEqualTo(email);

        verify(supabaseAuthClient).login(email, "Secret123!");
        verify(profileRepository).findById(UUID.fromString(id));
        verify(profileRepository).save(any(UserProfile.class));
        verifyNoMoreInteractions(supabaseAuthClient);
    }

    @Test
    @DisplayName("Debe registrar usuario exitosamente y crear perfil")
    void register_ValidData_SuccessAndCreatesProfile() {
        // Arrange
        RegisterRequest request = new RegisterRequest(
                "alice@acme.io",
                "Secret123!",
                "Alice",
                Sex.FEMALE,
                (short) 165,
                new BigDecimal("60"),
                LocalDate.of(1995, 1, 1),
                ActivityLevel.VERY_ACTIVE,
                DietType.LOW_CARB
        );
        String id = UUID.randomUUID().toString();

        when(supabaseAuthClient.signup(eq(request.email()), eq(request.password()), anyMap()))
                .thenReturn(Map.of());
        when(supabaseAuthClient.login(request.email(), request.password()))
                .thenReturn(tokenResponse(id, request.email()));
        when(profileRepository.findById(UUID.fromString(id))).thenReturn(Optional.empty());
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        // Act
        TokenResponse response = authService.register(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.access_token()).isEqualTo("at");
        assertThat(response.refresh_token()).isEqualTo("rt");

        verify(supabaseAuthClient).signup(eq(request.email()), eq(request.password()), anyMap());
        verify(supabaseAuthClient).login(request.email(), request.password());
        verify(profileRepository).findById(UUID.fromString(id));
        verify(profileRepository).save(any(UserProfile.class));
        verifyNoMoreInteractions(supabaseAuthClient);
    }

    @Test
    @DisplayName("Debe actualizar correctamente el perfil del usuario")
    void updateProfile_UpdatesFieldsAndSaves() {
        // Arrange
        UUID id = UUID.randomUUID();
        String email = "user@test.com";
        Jwt jwt = buildJwt(id.toString(), email);

        when(profileRepository.findById(id)).thenReturn(Optional.empty());
        when(profileRepository.existsByUsernameIgnoreCase(anyString())).thenReturn(false);
        when(profileRepository.save(any(UserProfile.class))).thenAnswer(inv -> inv.getArgument(0));

        UpdateProfileRequest request = new UpdateProfileRequest(
                "NewName",
                Sex.MALE,
                (short) 180,
                new BigDecimal("80"),
                LocalDate.of(1990, 5, 20),
                ActivityLevel.VERY_ACTIVE,
                DietType.LOW_CARB
        );

        // Act
        UpdateProfileResponse response = authService.updateProfile(jwt, request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.id()).isEqualTo(id.toString());
        assertThat(response.username()).isEqualTo("newname");
        assertThat(response.sex()).isEqualTo("male");
        assertThat(response.height_cm()).isEqualTo((short) 180);
        assertThat(response.weight_kg()).isEqualTo(new BigDecimal("80"));
        assertThat(response.activity_level()).isEqualTo(ActivityLevel.VERY_ACTIVE.dbValue());
        assertThat(response.diet_type()).isEqualTo(DietType.LOW_CARB.dbValue());
        assertThat(response.updated_at()).isNotNull();

        verify(profileRepository, atLeastOnce()).save(any(UserProfile.class));
        verifyNoInteractions(supabaseAuthClient);
    }

    @Test
    @DisplayName("Debe eliminar la cuenta correctamente")
    void deleteAccount_RemovesProfileById() {
        // Arrange
        UUID id = UUID.randomUUID();
        Jwt jwt = buildJwt(id.toString(), "a@b.c");

        // Act
        authService.deleteAccount(jwt);

        // Assert
        verify(profileRepository).deleteById(id);
        verifyNoMoreInteractions(profileRepository);
        verifyNoInteractions(supabaseAuthClient);
    }

    @Test
    @DisplayName("Debe refrescar el token correctamente")
    void refresh_ReturnsNewToken() {
        // Arrange
        when(supabaseAuthClient.refresh("RREF"))
                .thenReturn(tokenResponse(UUID.randomUUID().toString(), "x@y.z"));

        // Act
        TokenResponse response = authService.refresh("RREF");

        // Assert
        assertThat(response.access_token()).isEqualTo("at");
        assertThat(response.refresh_token()).isEqualTo("rt");

        verify(supabaseAuthClient).refresh("RREF");
        verifyNoMoreInteractions(supabaseAuthClient);
        verifyNoInteractions(profileRepository);
    }
}
