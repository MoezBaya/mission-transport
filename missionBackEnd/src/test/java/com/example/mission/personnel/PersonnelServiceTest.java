package com.example.mission.personnel;

import com.example.mission.personnel.dto.PersonnelRequest;
import com.example.mission.personnel.dto.PersonnelResponse;
import com.example.mission.personnel.repository.PersonnelRepository;
import com.example.mission.personnel.service.PersonnelService;
import com.example.mission.user.entity.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonnelServiceTest {

    @Mock
    private PersonnelRepository personnelRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private PersonnelService personnelService;

    // ============================================================
    // SETUP : simuler un Directeur authentifié avant chaque test
    // ============================================================
    @BeforeEach
    void setUp() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "directeur@test.com",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_DIRECTEUR"))
                );
        SecurityContextHolder.getContext().setAuthentication(auth);
    }

    @AfterEach
    void tearDown() {
        SecurityContextHolder.clearContext();
    }

    // ============================================================
    // TEST 1 : listerTous
    // ============================================================
    @Test
    void listerTous_devraitRetournerListe() {
        ChefDeParc cp = new ChefDeParc();
        cp.setId(1L);
        cp.setNom("Ben Ali");
        cp.setPrenom("Ahmed");
        cp.setCin("12345678");
        cp.setEmail("ahmed@mail.com");

        when(personnelRepository.findAll()).thenReturn(List.of(cp));

        List<PersonnelResponse> result = personnelService.listerTous();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Ben Ali");
        assertThat(result.get(0).getRole()).isEqualTo("CHEF_DE_PARC");
    }

    // ============================================================
    // TEST 2 : ajouter — succès
    // ============================================================
    @Test
    void ajouter_devraitCreerUnPersonnel() {
        PersonnelRequest request = new PersonnelRequest();
        request.setNom("Ben Ali");
        request.setPrenom("Ahmed");
        request.setCin("12345678");
        request.setEmail("ahmed@mail.com");
        request.setMotDePasse("password123");
        request.setRole("CHEF_DE_PARC");

        when(personnelRepository.existsByCin("12345678")).thenReturn(false);
        when(personnelRepository.existsByEmail("ahmed@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(personnelRepository.save(any(Personnel.class)))
                .thenAnswer(inv -> {
                    Personnel p = inv.getArgument(0);
                    p.setId(1L);
                    return p;
                });

        PersonnelResponse response = personnelService.ajouter(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNom()).isEqualTo("Ben Ali");
        assertThat(response.getRole()).isEqualTo("CHEF_DE_PARC");
        verify(personnelRepository, times(1)).save(any(Personnel.class));
    }

    // ============================================================
    // TEST 3 : ajouter — CIN existant
    // ============================================================
    @Test
    void ajouter_devraitLancerExceptionSiCinExiste() {
        PersonnelRequest request = new PersonnelRequest();
        request.setNom("X");
        request.setPrenom("Y");
        request.setCin("12345678");
        request.setEmail("x@mail.com");
        request.setMotDePasse("password123");
        request.setRole("CHEF_DE_PARC");

        when(personnelRepository.existsByCin("12345678")).thenReturn(true);

        assertThatThrownBy(() -> personnelService.ajouter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CIN existe déjà");

        verify(personnelRepository, never()).save(any());
    }

    // ============================================================
    // TEST 4 : ajouter — mot de passe trop court
    // ============================================================
    @Test
    void ajouter_devraitLancerExceptionSiMdpTropCourt() {
        PersonnelRequest request = new PersonnelRequest();
        request.setNom("X");
        request.setPrenom("Y");
        request.setCin("12345678");
        request.setEmail("x@mail.com");
        request.setMotDePasse("123");   // < 6
        request.setRole("CHEF_DE_PARC");

        assertThatThrownBy(() -> personnelService.ajouter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("6 caractères");
    }

    // ============================================================
    // TEST 5 : ajouter — rôle CHAUFFEUR refusé
    // ============================================================
    @Test
    void ajouter_devraitRefuserRoleChauffeur() {
        PersonnelRequest request = new PersonnelRequest();
        request.setNom("X");
        request.setPrenom("Y");
        request.setCin("12345678");
        request.setEmail("x@mail.com");
        request.setMotDePasse("password123");
        request.setRole("CHAUFFEUR");

        when(personnelRepository.existsByCin(any())).thenReturn(false);
        when(personnelRepository.existsByEmail(any())).thenReturn(false);

        assertThatThrownBy(() -> personnelService.ajouter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CHAUFFEUR");
    }

    // ============================================================
    // TEST 6 : trouverParId — succès
    // ============================================================
    @Test
    void trouverParId_devraitRetournerPersonnel() {
        Directeur d = new Directeur();
        d.setId(5L);
        d.setNom("Trabelsi");
        d.setPrenom("Sami");
        d.setCin("87654321");
        d.setEmail("sami@mail.com");

        when(personnelRepository.findById(5L)).thenReturn(Optional.of(d));

        PersonnelResponse response = personnelService.trouverParId(5L);

        assertThat(response.getId()).isEqualTo(5L);
        assertThat(response.getRole()).isEqualTo("DIRECTEUR");
    }

    // ============================================================
    // TEST 7 : trouverParId — introuvable
    // ============================================================
    @Test
    void trouverParId_devraitLancerExceptionSiIntrouvable() {
        when(personnelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personnelService.trouverParId(99L))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("introuvable");
    }

    // ============================================================
    // TEST 8 : supprimer — succès
    // ============================================================
    @Test
    void supprimer_devraitSupprimer() {
        when(personnelRepository.existsById(1L)).thenReturn(true);

        personnelService.supprimer(1L);

        verify(personnelRepository, times(1)).deleteById(1L);
    }

    // ============================================================
    // TEST 9 : supprimer — introuvable
    // ============================================================
    @Test
    void supprimer_devraitLancerExceptionSiIntrouvable() {
        when(personnelRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> personnelService.supprimer(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // ============================================================
    // TEST 10 : accès refusé si pas Directeur
    // ============================================================
    @Test
    void ajouter_devraitRefuserSiPasDirecteur() {
        // Simuler un ChefParc (pas Directeur)
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "chefparc@test.com",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_CHEF_DE_PARC"))
                );
        SecurityContextHolder.getContext().setAuthentication(auth);

        PersonnelRequest request = new PersonnelRequest();
        request.setNom("X");
        request.setPrenom("Y");
        request.setCin("12345678");
        request.setEmail("x@mail.com");
        request.setMotDePasse("password123");
        request.setRole("CHEF_DE_PARC");

        assertThatThrownBy(() -> personnelService.ajouter(request))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }
}