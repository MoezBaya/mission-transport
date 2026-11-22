package com.example.mission.vehicule;

import com.example.mission.shared.security.JwtAuthenticationFilter;
import com.example.mission.shared.security.JwtService;
import com.example.mission.user.repository.UtilisateurRepository;
import com.example.mission.vehicule.controller.VehiculeController;
import com.example.mission.vehicule.dto.VehiculeRequest;
import com.example.mission.vehicule.dto.VehiculeResponse;
import com.example.mission.vehicule.entity.EtatVehicule;
import com.example.mission.vehicule.service.VehiculeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(VehiculeController.class)
@AutoConfigureMockMvc(addFilters = false)
class VehiculeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private VehiculeService vehiculeService;

    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private UtilisateurRepository utilisateurRepository;

    @Test
    void listerTous_devraitRetourner200() throws Exception {
        VehiculeResponse r = new VehiculeResponse(
                1L, "123 TUN 4567", "Renault", "Kangoo",
                15000L, EtatVehicule.DISPONIBLE , "suvp"
        );
        when(vehiculeService.listerTous()).thenReturn(List.of(r));

        mockMvc.perform(get("/v1/api/vehicule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].matricule").value("123 TUN 4567"));
    }

    @Test
    void ajouter_devraitRetourner201() throws Exception {
        VehiculeRequest request = new VehiculeRequest();
        request.setMatricule("123 TUN 4567");
        request.setMarque("Renault");
        request.setModele("Kangoo");
        request.setKilometrage(15000L);
        request.setEtat(EtatVehicule.DISPONIBLE);

        VehiculeResponse response = new VehiculeResponse(
                1L, "123 TUN 4567", "Renault", "Kangoo",
                15000L, EtatVehicule.DISPONIBLE, "brek up"
        );
        when(vehiculeService.ajouter(any(VehiculeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/vehicule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    void modifier_devraitRetourner200() throws Exception {
        VehiculeRequest request = new VehiculeRequest();
        request.setMatricule("123 TUN 4567");
        request.setMarque("Peugeot");

        VehiculeResponse response = new VehiculeResponse(
                1L, "123 TUN 4567", "Peugeot", "Partner",
                20000L, EtatVehicule.EN_MISSION , "pick-up"
        );
        when(vehiculeService.modifier(any(), any())).thenReturn(response);

        mockMvc.perform(put("/v1/api/vehicule/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk());
    }

    @Test
    void supprimer_devraitRetourner200() throws Exception {
        mockMvc.perform(delete("/v1/api/vehicule/1"))
                .andExpect(status().isOk());
    }
}