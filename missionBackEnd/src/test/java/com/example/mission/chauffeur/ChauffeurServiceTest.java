package com.example.mission.chauffeur;

import com.example.mission.chauffeur.dto.ChauffeurRequest;
import com.example.mission.chauffeur.dto.ChauffeurResponse;
import com.example.mission.chauffeur.repository.ChauffeurRepository;
import com.example.mission.chauffeur.service.ChauffeurService;
import com.example.mission.user.entity.Chauffeur;
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
class ChauffeurServiceTest {

    @Mock
    private ChauffeurRepository chauffeurRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private ChauffeurService chauffeurService;

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

    // TEST 1 : listerTous
    @Test
    void listerTous_devraitRetournerListe() {
        Chauffeur c = new Chauffeur();
        c.setId(1L);
        c.setNom("Mansour");
        c.setPrenom("Karim");
        c.setCin("11223344");
        c.setEmail("karim@mail.com");
        c.setNumPermis("P123456");
        c.setDisponible(true);

        when(chauffeurRepository.findAll()).thenReturn(List.of(c));

        List<ChauffeurResponse> result = chauffeurService.listerTous();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNom()).isEqualTo("Mansour");
        assertThat(result.get(0).getNumeroPermis()).isEqualTo("P123456");
    }

    // TEST 2 : ajouter — succès
    @Test
    void ajouter_devraitCreerUnChauffeur() {
        ChauffeurRequest request = new ChauffeurRequest();
        request.setNom("Mansour");
        request.setPrenom("Karim");
        request.setCin("11223344");
        request.setEmail("karim@mail.com");
        request.setMotDePasse("password123");
        request.setNumeroPermis("P123456");
        request.setCategoriePermis("C");
        request.setDisponible(true);

        when(chauffeurRepository.existsByCin("11223344")).thenReturn(false);
        when(chauffeurRepository.existsByEmail("karim@mail.com")).thenReturn(false);
        when(passwordEncoder.encode("password123")).thenReturn("encoded");
        when(chauffeurRepository.save(any(Chauffeur.class)))
                .thenAnswer(inv -> {
                    Chauffeur c = inv.getArgument(0);
                    c.setId(1L);
                    return c;
                });

        ChauffeurResponse response = chauffeurService.ajouter(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getNumeroPermis()).isEqualTo("P123456");
        verify(chauffeurRepository, times(1)).save(any(Chauffeur.class));
    }

    // TEST 3 : ajouter — numéro permis obligatoire
    @Test
    void ajouter_devraitLancerExceptionSiNumeroPermisManquant() {
        ChauffeurRequest request = new ChauffeurRequest();
        request.setNom("Mansour");
        request.setPrenom("Karim");
        request.setCin("11223344");
        request.setEmail("karim@mail.com");
        request.setMotDePasse("password123");
        // numeroPermis null

        assertThatThrownBy(() -> chauffeurService.ajouter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("permis");
    }

    // TEST 4 : ajouter — CIN existant
    @Test
    void ajouter_devraitLancerExceptionSiCinExiste() {
        ChauffeurRequest request = new ChauffeurRequest();
        request.setNom("X");
        request.setPrenom("Y");
        request.setCin("11223344");
        request.setEmail("x@mail.com");
        request.setMotDePasse("password123");
        request.setNumeroPermis("P123");

        when(chauffeurRepository.existsByCin("11223344")).thenReturn(true);

        assertThatThrownBy(() -> chauffeurService.ajouter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CIN existe déjà");
    }

    // TEST 5 : modifier — Directeur OK
    @Test
    void modifier_devraitModifierSiDirecteur() {
        Chauffeur c = new Chauffeur();
        c.setId(1L);
        c.setNom("Ancien");
        c.setPrenom("Prenom");
        c.setCin("11223344");
        c.setEmail("x@mail.com");

        ChauffeurRequest request = new ChauffeurRequest();
        request.setNom("Nouveau");
        request.setPrenom("Prenom");
        request.setCin("11223344");
        request.setEmail("x@mail.com");
        request.setNumeroPermis("P123");

        when(chauffeurRepository.findById(1L)).thenReturn(Optional.of(c));
        when(chauffeurRepository.save(any(Chauffeur.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ChauffeurResponse response = chauffeurService.modifier(1L, request);

        assertThat(response.getNom()).isEqualTo("Nouveau");
    }

    // TEST 6 : modifier — refusé si pas Directeur
    @Test
    void modifier_devraitRefuserSiPasDirecteur() {
        // Simuler ChefParc
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "chefparc@test.com",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_CHEF_PARC"))
                );
        SecurityContextHolder.getContext().setAuthentication(auth);

        ChauffeurRequest request = new ChauffeurRequest();
        request.setNom("X");
        request.setPrenom("Y");
        request.setCin("11223344");
        request.setEmail("x@mail.com");

        assertThatThrownBy(() -> chauffeurService.modifier(1L, request))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }

    // TEST 7 : supprimer — succès
    @Test
    void supprimer_devraitSupprimer() {
        when(chauffeurRepository.existsById(1L)).thenReturn(true);

        chauffeurService.supprimer(1L);

        verify(chauffeurRepository, times(1)).deleteById(1L);
    }

    // TEST 8 : supprimer — refusé si pas Directeur
    @Test
    void supprimer_devraitRefuserSiPasDirecteur() {
        UsernamePasswordAuthenticationToken auth =
                new UsernamePasswordAuthenticationToken(
                        "chefparc@test.com",
                        null,
                        List.of(new SimpleGrantedAuthority("ROLE_CHEF_PARC"))
                );
        SecurityContextHolder.getContext().setAuthentication(auth);

        assertThatThrownBy(() -> chauffeurService.supprimer(1L))
                .isInstanceOf(org.springframework.security.access.AccessDeniedException.class);
    }
}
