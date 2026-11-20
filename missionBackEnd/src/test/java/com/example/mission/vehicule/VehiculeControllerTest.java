/*package com.example.mission.vehicule;


import com.example.mission.vehicule.service.VehiculeService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
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

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private VehiculeService vehiculeService;

    @Test
    void listerTous_devraitRetourner200() throws Exception {
        VehiculeResponse r = new VehiculeResponse(
                1L, "123 TUN 4567", "Renault", "Kangoo",
                2020, "Camion", 1500.0, 3, true, "BON"
        );
        when(vehiculeService.listerTous()).thenReturn(List.of(r));

        mockMvc.perform(get("/v1/api/vehicule"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].immatriculation").value("123 TUN 4567"));
    }

    @Test
    void ajouter_devraitRetourner201() throws Exception {
        VehiculeRequest request = new VehiculeRequest();
        request.setImmatriculation("123 TUN 4567");
        request.setMarque("Renault");
        request.setModele("Kangoo");
        request.setAnnee(2020);
        request.setDisponible(true);

        VehiculeResponse response = new VehiculeResponse(
                1L, "123 TUN 4567", "Renault", "Kangoo",
                2020, "Camion", 1500.0, 3, true, "BON"
        );
        when(vehiculeService.ajouter(any(VehiculeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/vehicule")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.immatriculation").value("123 TUN 4567"));
    }

    @Test
    void supprimer_devraitRetourner200() throws Exception {
        mockMvc.perform(delete("/v1/api/vehicule/1"))
                .andExpect(status().isOk());
    }
}*/