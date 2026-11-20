package com.example.mission.chauffeur;

import com.example.mission.chauffeur.controller.ChauffeurController;
import com.example.mission.chauffeur.dto.ChauffeurRequest;
import com.example.mission.chauffeur.dto.ChauffeurResponse;
import com.example.mission.chauffeur.service.ChauffeurService;
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

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ChauffeurController.class)
@AutoConfigureMockMvc(addFilters = false)
class ChauffeurControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private UtilisateurRepository utilisateurRepository;
    @MockitoBean
    private PasswordEncoder passwordEncoder;

    @MockitoBean
    private ChauffeurService chauffeurService;

    // ⚡ AJOUT : mocker JwtService et JwtAuthenticationFilter
    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Test
    void listerTous_devraitRetourner200() throws Exception {
        ChauffeurResponse r = new ChauffeurResponse(
                1L, "Mansour", "Karim", "11223344",
                "karim@mail.com", "20111222", "P123456",
                "C", LocalDate.of(2027, 6, 15), true
        );
        when(chauffeurService.listerTous()).thenReturn(List.of(r));

        mockMvc.perform(get("/v1/api/chauffeur"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nom").value("Mansour"))
                .andExpect(jsonPath("$[0].numeroPermis").value("P123456"));
    }

    @Test
    void ajouter_devraitRetourner201() throws Exception {
        ChauffeurRequest request = new ChauffeurRequest();
        request.setNom("Mansour");
        request.setPrenom("Karim");
        request.setCin("11223344");
        request.setEmail("karim@mail.com");
        request.setMotDePasse("password123");
        request.setNumeroPermis("P123456");
        request.setCategoriePermis("C");
        request.setDisponible(true);

        ChauffeurResponse response = new ChauffeurResponse(
                1L, "Mansour", "Karim", "11223344",
                "karim@mail.com", "20111222", "P123456",
                "C", LocalDate.of(2027, 6, 15), true
        );
        when(chauffeurService.ajouter(any(ChauffeurRequest.class))).thenReturn(response);

        mockMvc.perform(post("/v1/api/chauffeur")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.numeroPermis").value("P123456"));
    }

    @Test
    void modifier_devraitRetourner200() throws Exception {
        ChauffeurRequest request = new ChauffeurRequest();
        request.setNom("Nouveau");

        ChauffeurResponse response = new ChauffeurResponse(
                1L, "Nouveau", "Karim", "11223344",
                "karim@mail.com", "20111222", "P123456",
                "C", LocalDate.of(2027, 6, 15), false
        );
        when(chauffeurService.modifier(any(), any())).thenReturn(response);

        mockMvc.perform(put("/v1/api/chauffeur/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nom").value("Nouveau"));
    }

    @Test
    void supprimer_devraitRetourner200() throws Exception {
        mockMvc.perform(delete("/v1/api/chauffeur/1"))
                .andExpect(status().isOk());
    }
}
