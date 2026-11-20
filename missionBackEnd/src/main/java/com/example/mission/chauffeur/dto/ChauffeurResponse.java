package com.example.mission.chauffeur.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDate;

@Getter
@AllArgsConstructor
public class ChauffeurResponse {
    private Long id;                  // ⚡ TOUJOURS id en premier
    private String nom;
    private String prenom;
    private String cin;
    private String email;
    private String telephone;
    private String numeroPermis;
    private String categoriePermis;
    private LocalDate dateValiditePermis;
    private Boolean disponible;

}