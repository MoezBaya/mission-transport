package com.example.mission.auth;

import com.example.mission.auth.dto.LoginRequest;
import com.example.mission.auth.dto.LoginResponse;
import com.example.mission.auth.service.AuthService;
import com.example.mission.shared.security.JwtService;
import com.example.mission.user.entity.Directeur;
import com.example.mission.user.entity.Role;
import com.example.mission.user.repository.UtilisateurRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private UtilisateurRepository utilisateurRepository;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private AuthService authService;

    // ============================================================
    // TEST 1 : login — succès
    // ============================================================
    @Test
    void login_devraitRetournerTokenEtInfos() {
        // Arrange
        LoginRequest request = new LoginRequest();
        request.setEmail("directeur@mail.com");
        request.setMotDePasse("password123");

        Directeur d = new Directeur();
        d.setId(1L);
        d.setNom("Ben Ali");
        d.setPrenom("Ahmed");
        d.setEmail("directeur@mail.com");
        d.setTelephone("20123456");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(utilisateurRepository.findByEmail("directeur@mail.com"))
                .thenReturn(Optional.of(d));
        when(jwtService.generateToken(d)).thenReturn("fake.jwt.token");

        // Act
        LoginResponse response = authService.login(request);

        // Assert
        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("fake.jwt.token");
        assertThat(response.getEmail()).isEqualTo("directeur@mail.com");
        assertThat(response.getNom()).isEqualTo("Ben Ali");
        assertThat(response.getPrenom()).isEqualTo("Ahmed");
        assertThat(response.getRole()).isEqualTo(Role.DIRECTEUR);

        // Verify
        verify(authenticationManager, times(1)).authenticate(any());
        verify(jwtService, times(1)).generateToken(d);
    }

    // ============================================================
    // TEST 2 : login — mauvais mot de passe
    // ============================================================
    @Test
    void login_devraitLancerExceptionSiMauvaisMdp() {
        LoginRequest request = new LoginRequest();
        request.setEmail("x@mail.com");
        request.setMotDePasse("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class)
                .hasMessageContaining("Bad credentials");

        verify(jwtService, never()).generateToken(any());
    }

    // ============================================================
    // TEST 3 : login — utilisateur introuvable
    // ============================================================
    @Test
    void login_devraitLancerExceptionSiUtilisateurIntrouvable() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@mail.com");
        request.setMotDePasse("password123");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(utilisateurRepository.findByEmail("ghost@mail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("introuvable");

        verify(jwtService, never()).generateToken(any());
    }

    // ============================================================
    // TEST 4 : login — email null
    // ============================================================
    @Test
    void login_devraitLancerExceptionSiEmailNull() {
        LoginRequest request = new LoginRequest();
        request.setEmail(null);
        request.setMotDePasse("password123");

        // Le service doit vérifier avant d'appeler authenticate
        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(Exception.class);
    }

    // ============================================================
    // TEST 5 : login — mot de passe null
    // ============================================================
    @Test
    void login_devraitLancerExceptionSiMotDePasseNull() {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setMotDePasse(null);

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(Exception.class);
    }

    // ============================================================
    // TEST 6 : logout — ne doit pas planter
    // ============================================================
    @Test
    void logout_devraitNePasPlanter() {
        assertThatCode(() -> authService.logout())
                .doesNotThrowAnyException();
    }

    // ============================================================
    // TEST 7 : login — vérifier que le token est bien généré
    // ============================================================
    @Test
    void login_devraitAppelerJwtService() {
        LoginRequest request = new LoginRequest();
        request.setEmail("directeur@mail.com");
        request.setMotDePasse("password123");

        Directeur d = new Directeur();
        d.setId(1L);
        d.setNom("Ben Ali");
        d.setPrenom("Ahmed");
        d.setEmail("directeur@mail.com");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(utilisateurRepository.findByEmail("directeur@mail.com"))
                .thenReturn(Optional.of(d));
        when(jwtService.generateToken(d)).thenReturn("token");

        authService.login(request);

        // Vérifier que jwtService a bien été appelé
        verify(jwtService, times(1)).generateToken(d);
    }
}
