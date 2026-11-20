package com.example.mission.personnel;

import com.example.mission.chauffeur.service.ChauffeurService;
import com.example.mission.personnel.controller.PersonnelController;
import com.example.mission.personnel.dto.PersonnelRequest;
import com.example.mission.personnel.dto.PersonnelResponse;
import com.example.mission.personnel.service.PersonnelService;
import com.example.mission.shared.security.JwtAuthenticationFilter;
import com.example.mission.shared.security.JwtService;
import com.example.mission.user.repository.UtilisateurRepository;
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

@WebMvcTest(PersonnelController.class)
@AutoConfigureMockMvc(addFilters = false)
class PersonnelControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UtilisateurRepository utilisateurRepository;
    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private PersonnelService personnelService;



    // ⚡ AJOUT : mocker JwtService et JwtAuthenticationFilter
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    // TEST 1 : GET /personnel → 200
    @Test
    void listerTous_devraitRetourner200() throws Exception {
        PersonnelResponse r = new PersonnelResponse(
                1L, "Ben Ali", "Ahmed", "12345678",
                "ahmed@mail.com", "20123456", "CHEF_DE_PARC"
        );
        when(personnelService.listerTous()).thenReturn(List.of(r));

        mockMvc.perform(get("/v1/api/personnel"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Ben Ali"))
                .andExpect(jsonPath("$[0].role").value("CHEF_DE_PARC"));
    }

    // TEST 2 : POST /personnel → 201
    @Test
    void ajouter_devraitRetourner201() throws Exception {
        PersonnelRequest request = new PersonnelRequest();
        request.setNom("Ben Ali");
        request.setPrenom("Ahmed");
        request.setCin("12345678");
        request.setEmail("ahmed@mail.com");
        request.setMotDePasse("password123");
        request.setRole("CHEF_DE_PARC");

        PersonnelResponse response = new PersonnelResponse(
                1L, "Ben Ali", "Ahmed", "12345678",
                "ahmed@mail.com", "20123456", "CHEF_DE_PARC"
        );
        when(personnelService.ajouter(any(PersonnelRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/personnel")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.nom").value("Ben Ali"));
    }

    // TEST 3 : PUT /personnel/{id} → 200
    @Test
    void modifier_devraitRetourner200() throws Exception {
        PersonnelRequest request = new PersonnelRequest();
        request.setNom("Nouveau");

        PersonnelResponse response = new PersonnelResponse(
                1L, "Nouveau", "Ahmed", "12345678",
                "ahmed@mail.com", "20123456", "CHEF_DE_PARC"
        );
        when(personnelService.modifier(any(), any())).thenReturn(response);

        mockMvc.perform(put("/v1/api/personnel/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Nouveau"));
    }

    // TEST 4 : DELETE /personnel/{id} → 200
    @Test
    void supprimer_devraitRetourner200() throws Exception {
        mockMvc.perform(delete("/v1/api/personnel/1"))
                .andExpect(status().isOk());
    }
}
