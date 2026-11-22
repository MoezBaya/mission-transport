package com.example.mission.shared.security;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

import static org.assertj.core.api.Assertions.*;

class SecurityServiceTest {

    private SecurityService securityService;

    @BeforeEach
    void setUp() {
        securityService = new SecurityService();
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ============================================================
    // TEST 1 : verifierAuthentifie — OK
    // ============================================================
    @Test
    void verifierAuthentifie_devraitPasserSiAuthentifie() {
        // Arrange : simuler un utilisateur authentifié
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_DIRECTEUR"))
                )
        );

        // Act + Assert : ne doit pas lancer d'exception
        assertThatCode(() -> securityService.verifierAuthentifie())
                .doesNotThrowAnyException();
    }

    // ============================================================
    // TEST 2 : verifierAuthentifie — KO (pas d'auth)
    // ============================================================
    @Test
    void verifierAuthentifie_devraitLancerExceptionSiNonAuthentifie() {
        // Arrange : pas d'authentification
        SecurityContextHolder.clearContext();

        // Act + Assert
        assertThatThrownBy(() -> securityService.verifierAuthentifie())
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("authentifié");
    }

    // ============================================================
    // TEST 3 : verifierAuthentifie — KO (anonymousUser)
    // ============================================================
    @Test
    void verifierAuthentifie_devraitLancerExceptionSiAnonymous() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "anonymousUser", null, List.of()
                )
        );

        assertThatThrownBy(() -> securityService.verifierAuthentifie())
                .isInstanceOf(AccessDeniedException.class);
    }

    // ============================================================
    // TEST 4 : verifierRole — OK (bon rôle)
    // ============================================================
    @Test
    void verifierRole_devraitPasserSiBonRole() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_DIRECTEUR"))
                )
        );

        assertThatCode(() -> securityService.verifierRole("DIRECTEUR"))
                .doesNotThrowAnyException();
    }

    // ============================================================
    // TEST 5 : verifierRole — OK (parmi plusieurs rôles)
    // ============================================================
    @Test
    void verifierRole_devraitPasserSiUnDesRolesCorrespond() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CHEF_DE_PARC"))
                )
        );

        assertThatCode(() ->
                securityService.verifierRole("DIRECTEUR", "CHEF_DE_PARC"))
                .doesNotThrowAnyException();
    }

    // ============================================================
    // TEST 6 : verifierRole — KO (mauvais rôle)
    // ============================================================
    @Test
    void verifierRole_devraitLancerExceptionSiMauvaisRole() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CHEF_DE_PARC"))
                )
        );

        assertThatThrownBy(() -> securityService.verifierRole("DIRECTEUR"))
                .isInstanceOf(AccessDeniedException.class)
                .hasMessageContaining("Accès refusé");
    }

    // ============================================================
    // TEST 7 : verifierRole — KO (pas auth)
    // ============================================================
    @Test
    void verifierRole_devraitLancerExceptionSiNonAuthentifie() {
        SecurityContextHolder.clearContext();

        assertThatThrownBy(() -> securityService.verifierRole("DIRECTEUR"))
                .isInstanceOf(AccessDeniedException.class);
    }

    // ============================================================
    // TEST 8 : getEmailConnecte — OK
    // ============================================================
    @Test
    void getEmailConnecte_devraitRetournerEmail() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "directeur@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_DIRECTEUR"))
                )
        );

        String email = securityService.getEmailConnecte();

        assertThat(email).isEqualTo("directeur@test.com");
    }

    // ============================================================
    // TEST 9 : aLeRole — OK
    // ============================================================
    @Test
    void aLeRole_devraitRetournerTrueSiBonRole() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_DIRECTEUR"))
                )
        );

        assertThat(securityService.aLeRole("DIRECTEUR")).isTrue();
    }

    // ============================================================
    // TEST 10 : aLeRole — KO
    // ============================================================
    @Test
    void aLeRole_devraitRetournerFalseSiMauvaisRole() {
        SecurityContextHolder.getContext().setAuthentication(
                new UsernamePasswordAuthenticationToken(
                        "user@test.com", null,
                        List.of(new SimpleGrantedAuthority("ROLE_CHEF_DE_PARC"))
                )
        );

        assertThat(securityService.aLeRole("DIRECTEUR")).isFalse();
    }
}
