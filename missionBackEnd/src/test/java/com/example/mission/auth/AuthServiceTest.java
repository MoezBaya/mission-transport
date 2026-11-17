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

    // TEST 1 : login — succès
    @Test
    void login_devraitRetournerTokenEtInfos() {
        LoginRequest request = new LoginRequest();
        request.setEmail("directeur@mail.com");
        request.setMotDePasse("password123");

        Directeur d = new Directeur();
        d.setId(1L);
        d.setNom("Ben Ali");
        d.setPrenom("Ahmed");
        d.setEmail("directeur@mail.com");

        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenReturn(null);
        when(utilisateurRepository.findByEmail("directeur@mail.com"))
                .thenReturn(Optional.of(d));
        when(jwtService.generateToken(d)).thenReturn("fake.jwt.token");

        LoginResponse response = authService.login(request);

        assertThat(response).isNotNull();
        assertThat(response.getToken()).isEqualTo("fake.jwt.token");
        assertThat(response.getEmail()).isEqualTo("directeur@mail.com");
        assertThat(response.getRole()).isEqualTo(Role.DIRECTEUR);
    }

    // TEST 2 : login — mauvais mot de passe
    @Test
    void login_devraitLancerExceptionSiMauvaisMdp() {
        LoginRequest request = new LoginRequest();
        request.setEmail("x@mail.com");
        request.setMotDePasse("wrong");

        when(authenticationManager.authenticate(any()))
                .thenThrow(new BadCredentialsException("Bad credentials"));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadCredentialsException.class);
    }

    // TEST 3 : login — utilisateur introuvable après auth
    @Test
    void login_devraitLancerExceptionSiUtilisateurIntrouvable() {
        LoginRequest request = new LoginRequest();
        request.setEmail("ghost@mail.com");
        request.setMotDePasse("password123");

        when(authenticationManager.authenticate(any())).thenReturn(null);
        when(utilisateurRepository.findByEmail("ghost@mail.com"))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(RuntimeException.class);
    }
}
