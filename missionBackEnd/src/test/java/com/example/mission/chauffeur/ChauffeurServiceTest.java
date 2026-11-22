package com.example.mission.chauffeur;

import com.example.mission.chauffeur.dto.ChauffeurRequest;
import com.example.mission.chauffeur.dto.ChauffeurResponse;
import com.example.mission.chauffeur.repository.ChauffeurRepository;
import com.example.mission.chauffeur.service.ChauffeurService;
import com.example.mission.shared.security.SecurityService;
import com.example.mission.shared.validator.UtilisateurValidator;
import com.example.mission.user.entity.Chauffeur;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
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

    @Mock
    private SecurityService securityService;

    @Mock
    private UtilisateurValidator utilisateurValidator;

    @InjectMocks
    private ChauffeurService chauffeurService;

    // TEST 1 : listerTous — OK
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
    }

    // TEST 2 : ajouter — OK
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

        doNothing().when(securityService).verifierRole(any());
        doNothing().when(utilisateurValidator).validerChampsObligatoires(any(), any(), any(), any(), any());
        doNothing().when(utilisateurValidator).verifierUnicite(any(), any());

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
    }

    // TEST 3 : ajouter — KO si numéro permis manquant
    @Test
    void ajouter_devraitLancerExceptionSiNumeroPermisManquant() {
        ChauffeurRequest request = new ChauffeurRequest();
        request.setNom("Mansour");
        request.setPrenom("Karim");
        request.setCin("11223344");
        request.setEmail("karim@mail.com");
        request.setMotDePasse("password123");
        request.setNumeroPermis(null);

        assertThatThrownBy(() -> chauffeurService.ajouter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("permis");
    }

    // TEST 4 : modifier — OK
    @Test
    void modifier_devraitModifierSiDirecteur() {
        Chauffeur c = new Chauffeur();
        c.setId(1L);
        c.setNom("Ancien");
        c.setCin("11223344");
        c.setEmail("x@mail.com");

        ChauffeurRequest request = new ChauffeurRequest();
        request.setNom("Nouveau");
        request.setPrenom("Prenom");
        request.setCin("11223344");
        request.setEmail("x@mail.com");
        request.setNumeroPermis("P123");

        doNothing().when(securityService).verifierRole(any());
        doNothing().when(utilisateurValidator).validerChampsModification(any(), any());
        doNothing().when(utilisateurValidator).verifierUniciteModification(any(), any(), any(), any());

        when(chauffeurRepository.findById(1L)).thenReturn(Optional.of(c));
        when(chauffeurRepository.save(any(Chauffeur.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        ChauffeurResponse response = chauffeurService.modifier(1L, request);

        assertThat(response.getNom()).isEqualTo("Nouveau");
    }

    // TEST 5 : modifier — KO si pas Directeur
    @Test
    void modifier_devraitRefuserSiPasDirecteur() {
        doThrow(new AccessDeniedException("Accès refusé"))
                .when(securityService).verifierRole(any());

        ChauffeurRequest request = new ChauffeurRequest();

        assertThatThrownBy(() -> chauffeurService.modifier(1L, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    // TEST 6 : supprimer — OK
    @Test
    void supprimer_devraitSupprimer() {
        when(chauffeurRepository.existsById(1L)).thenReturn(true);

        chauffeurService.supprimer(1L);

        verify(chauffeurRepository, times(1)).deleteById(1L);
    }

    // TEST 7 : supprimer — KO si pas Directeur
    @Test
    void supprimer_devraitRefuserSiPasDirecteur() {
        doThrow(new AccessDeniedException("Accès refusé"))
                .when(securityService).verifierRole(any());

        assertThatThrownBy(() -> chauffeurService.supprimer(1L))
                .isInstanceOf(AccessDeniedException.class);
    }

    // TEST 8 : trouverParId — OK
    @Test
    void trouverParId_devraitRetournerChauffeur() {
        Chauffeur c = new Chauffeur();
        c.setId(5L);
        c.setNom("Mansour");

        when(chauffeurRepository.findById(5L)).thenReturn(Optional.of(c));

        ChauffeurResponse response = chauffeurService.trouverParId(5L);

        assertThat(response.getId()).isEqualTo(5L);
    }
}
