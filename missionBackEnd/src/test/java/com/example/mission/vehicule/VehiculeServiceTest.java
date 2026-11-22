package com.example.mission.vehicule;

import com.example.mission.shared.security.SecurityService;
import com.example.mission.vehicule.dto.VehiculeRequest;
import com.example.mission.vehicule.dto.VehiculeResponse;
import com.example.mission.vehicule.entity.EtatVehicule;
import com.example.mission.vehicule.entity.Vehicule;
import com.example.mission.vehicule.repository.VehiculeRepository;
import com.example.mission.vehicule.service.VehiculeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.access.AccessDeniedException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculeServiceTest {

    @Mock
    private VehiculeRepository vehiculeRepository;

    @Mock
    private SecurityService securityService;

    @InjectMocks
    private VehiculeService vehiculeService;

    @Test
    void listerTous_devraitRetournerListe() {
        Vehicule v = new Vehicule();
        v.setId(1L);
        v.setMatricule("123 TUN 4567");
        v.setMarque("Renault");
        v.setModele("Kangoo");
        v.setKilometrage(15000L);
        v.setEtat(EtatVehicule.DISPONIBLE);

        when(vehiculeRepository.findAll()).thenReturn(List.of(v));

        List<VehiculeResponse> result = vehiculeService.listerTous();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMatricule()).isEqualTo("123 TUN 4567");
    }

    @Test
    void ajouter_devraitCreerUnVehicule() {
        VehiculeRequest request = new VehiculeRequest();
        request.setMatricule("123 TUN 4567");
        request.setMarque("Renault");
        request.setModele("Kangoo");
        request.setKilometrage(15000L);
        request.setEtat(EtatVehicule.DISPONIBLE);

        doNothing().when(securityService).verifierRole(any());
        when(vehiculeRepository.existsByMatricule("123 TUN 4567")).thenReturn(false);
        when(vehiculeRepository.save(any(Vehicule.class)))
                .thenAnswer(inv -> {
                    Vehicule v = inv.getArgument(0);
                    v.setId(1L);
                    return v;
                });

        VehiculeResponse response = vehiculeService.ajouter(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getMatricule()).isEqualTo("123 TUN 4567");
    }

    @Test
    void ajouter_devraitLancerExceptionSiMatriculeExiste() {
        VehiculeRequest request = new VehiculeRequest();
        request.setMatricule("123 TUN 4567");
        request.setMarque("Renault");
        request.setModele("Kangoo");

        doNothing().when(securityService).verifierRole(any());
        when(vehiculeRepository.existsByMatricule("123 TUN 4567")).thenReturn(true);

        assertThatThrownBy(() -> vehiculeService.ajouter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("matricule");
    }

    @Test
    void ajouter_devraitLancerExceptionSiMatriculeVide() {
        VehiculeRequest request = new VehiculeRequest();
        request.setMatricule("");
        request.setMarque("Renault");
        request.setModele("Kangoo");

        doNothing().when(securityService).verifierRole(any());

        assertThatThrownBy(() -> vehiculeService.ajouter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("matricule");
    }

    @Test
    void modifier_devraitModifierVehicule() {
        Vehicule v = new Vehicule();
        v.setId(1L);
        v.setMatricule("123 TUN 4567");
        v.setEtat(EtatVehicule.DISPONIBLE);

        VehiculeRequest request = new VehiculeRequest();
        request.setMatricule("123 TUN 4567");
        request.setMarque("Peugeot");
        request.setModele("Partner");
        request.setKilometrage(20000L);
        request.setEtat(EtatVehicule.EN_MISSION);

        doNothing().when(securityService).verifierRole(any());
        when(vehiculeRepository.findById(1L)).thenReturn(Optional.of(v));
        when(vehiculeRepository.save(any(Vehicule.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        VehiculeResponse response = vehiculeService.modifier(1L, request);

        assertThat(response.getMarque()).isEqualTo("Peugeot");
        assertThat(response.getEtat()).isEqualTo(EtatVehicule.EN_MISSION);

    }

    @Test
    void modifier_devraitRefuserSiPasDirecteur() {
        doThrow(new AccessDeniedException("Accès refusé"))
                .when(securityService).verifierRole(any());

        VehiculeRequest request = new VehiculeRequest();

        assertThatThrownBy(() -> vehiculeService.modifier(1L, request))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void supprimer_devraitSupprimer() {
        when(vehiculeRepository.existsById(1L)).thenReturn(true);

        vehiculeService.supprimer(1L);

        verify(vehiculeRepository, times(1)).deleteById(1L);
    }

    @Test
    void supprimer_devraitLancerExceptionSiIntrouvable() {
        when(vehiculeRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> vehiculeService.supprimer(99L))
                .isInstanceOf(IllegalArgumentException.class);
    }
}