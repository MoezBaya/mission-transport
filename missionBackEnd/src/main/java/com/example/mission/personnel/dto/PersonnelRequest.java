package com.example.mission.personnel.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PersonnelRequest {
    private String nom;
    private String prenom;
    private String cin;
    private String email;
    private String telephone;
    private String motDePasse;   // requis à la création, optionnel à la modification
    private String role;         // "CHEF_PARC" | "DIRECTEUR" | "DIRECTEUR_LOGISTIQUE"
}