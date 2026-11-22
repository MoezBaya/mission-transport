package com.example.mission.personnel;

import com.example.mission.personnel.dto.PersonnelRequest;
import com.example.mission.personnel.dto.PersonnelResponse;
import com.example.mission.personnel.repository.PersonnelRepository;
import com.example.mission.personnel.service.PersonnelService;
import com.example.mission.shared.security.SecurityService;
import com.example.mission.shared.validator.UtilisateurValidator;
import com.example.mission.user.entity.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;
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

    @Mock
    private SecurityService securityService;

    @Mock
    private UtilisateurValidator utilisateurValidator;

    @InjectMocks
    private PersonnelService personnelService;

    // TEST 1 : listerTous — OK
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
        verify(securityService).verifierAuthentifie();
    }

    // TEST 2 : listerTous — KO si pas authentifié
    @Test
    void listerTous_devraitLancerExceptionSiPasAuth() {
        doThrow(new AccessDeniedException("Non authentifié"))
                .when(securityService).verifierAuthentifie();

        assertThatThrownBy(() -> personnelService.listerTous())
                .isInstanceOf(AccessDeniedException.class);
    }

    // TEST 3 : ajouter — OK
    @Test
    void ajouter_devraitCreerUnPersonnel() {
        PersonnelRequest request = new PersonnelRequest();
        request.setNom("Ben Ali");
        request.setPrenom("Ahmed");
        request.setCin("12345678");
        request.setEmail("ahmed@mail.com");
        request.setMotDePasse("password123");
        request.setRole("CHEF_DE_PARC");

        doNothing().when(securityService).verifierRole(any());
        doNothing().when(utilisateurValidator).validerChampsObligatoires(any(), any(), any(), any(), any());
        doNothing().when(utilisateurValidator).verifierUnicite(any(), any());

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

    // TEST 4 : ajouter — KO si rôle invalide
    @Test
    void ajouter_devraitLancerExceptionSiRoleInvalide() {
        PersonnelRequest request = new PersonnelRequest();
        request.setNom("Ben Ali");
        request.setPrenom("Ahmed");
        request.setCin("12345678");
        request.setEmail("ahmed@mail.com");
        request.setMotDePasse("password123");
        request.setRole("INVALID_ROLE");

        doNothing().when(securityService).verifierRole(any());
        doNothing().when(utilisateurValidator).validerChampsObligatoires(any(), any(), any(), any(), any());
        doNothing().when(utilisateurValidator).verifierUnicite(any(), any());

        assertThatThrownBy(() -> personnelService.ajouter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rôle invalide");
    }

    // TEST 5 : ajouter — KO si CHAUFFEUR
    @Test
    void ajouter_devraitRefuserRoleChauffeur() {
        PersonnelRequest request = new PersonnelRequest();
        request.setNom("X");
        request.setPrenom("Y");
        request.setCin("12345678");
        request.setEmail("x@mail.com");
        request.setMotDePasse("password123");
        request.setRole("CHAUFFEUR");

        doNothing().when(securityService).verifierRole(any());
        doNothing().when(utilisateurValidator).validerChampsObligatoires(any(), any(), any(), any(), any());
        doNothing().when(utilisateurValidator).verifierUnicite(any(), any());

        assertThatThrownBy(() -> personnelService.ajouter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("CHAUFFEUR");
    }

    // TEST 6 : ajouter — KO si pas Directeur
    @Test
    void ajouter_devraitRefuserSiPasDirecteur() {
        doThrow(new AccessDeniedException("Accès refusé"))
                .when(securityService).verifierRole(any());

        PersonnelRequest request = new PersonnelRequest();
        assertThatThrownBy(() -> personnelService.ajouter(request))
                .isInstanceOf(AccessDeniedException.class);
    }

    // TEST 7 : trouverParId — OK
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

    // TEST 8 : trouverParId — KO
    @Test
    void trouverParId_devraitLancerExceptionSiIntrouvable() {
        when(personnelRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> personnelService.trouverParId(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }

    // TEST 9 : supprimer — OK
    @Test
    void supprimer_devraitSupprimer() {
        when(personnelRepository.existsById(1L)).thenReturn(true);

        personnelService.supprimer(1L);

        verify(personnelRepository, times(1)).deleteById(1L);
    }

    // TEST 10 : supprimer — KO
    @Test
    void supprimer_devraitLancerExceptionSiIntrouvable() {
        when(personnelRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> personnelService.supprimer(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}