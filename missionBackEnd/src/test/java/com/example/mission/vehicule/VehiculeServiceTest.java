/*package com.example.mission.vehicule;


import com.example.mission.vehicule.entity.Vehicule;

import com.example.mission.vehicule.repository.VehiculeRepository;
import com.example.mission.vehicule.service.VehiculeService;
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

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VehiculeServiceTest {

    @Mock
    private VehiculeRepository vehiculeRepository;

    @InjectMocks
    private VehiculeService vehiculeService;

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

    @Test
    void listerTous_devraitRetournerListe() {
        Vehicule v = new Vehicule();
        v.setId(1L);
        v.setImmatriculation("123 TUN 4567");
        v.setMarque("Renault");
        v.setModele("Kangoo");
        v.setDisponible(true);

        when(vehiculeRepository.findAll()).thenReturn(List.of(v));

        List<VehiculeResponse> result = vehiculeService.listerTous();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getImmatriculation()).isEqualTo("123 TUN 4567");
    }

    @Test
    void ajouter_devraitCreerUnVehicule() {
        VehiculeRequest request = new VehiculeRequest();
        request.setImmatriculation("123 TUN 4567");
        request.setMarque("Renault");
        request.setModele("Kangoo");
        request.setAnnee(2020);
        request.setDisponible(true);

        when(vehiculeRepository.existsByImmatriculation("123 TUN 4567")).thenReturn(false);
        when(vehiculeRepository.save(any(Vehicule.class)))
                .thenAnswer(inv -> {
                    Vehicule v = inv.getArgument(0);
                    v.setId(1L);
                    return v;
                });

        VehiculeResponse response = vehiculeService.ajouter(request);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getImmatriculation()).isEqualTo("123 TUN 4567");
    }

    @Test
    void ajouter_devraitLancerExceptionSiImmatriculationExiste() {
        VehiculeRequest request = new VehiculeRequest();
        request.setImmatriculation("123 TUN 4567");
        request.setMarque("Renault");
        request.setModele("Kangoo");

        when(vehiculeRepository.existsByImmatriculation("123 TUN 4567")).thenReturn(true);

        assertThatThrownBy(() -> vehiculeService.ajouter(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("immatriculation");
    }

    @Test
    void supprimer_devraitSupprimer() {
        when(vehiculeRepository.existsById(1L)).thenReturn(true);

        vehiculeService.supprimer(1L);

        verify(vehiculeRepository, times(1)).deleteById(1L);
    }
}
*/