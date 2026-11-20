package com.example.mission.chauffeur.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class ChauffeurRequest {

    // Hérités de Utilisateur
    private String nom;
    private String prenom;
    private String cin;
    private String email;
    private String telephone;
    private String motDePasse;   // requis à la création, optionnel à la modification

    // Spécifiques Chauffeur
    private String numeroPermis;
    private String categoriePermis;
    private LocalDate dateValiditePermis;
    private Boolean disponible;
}
