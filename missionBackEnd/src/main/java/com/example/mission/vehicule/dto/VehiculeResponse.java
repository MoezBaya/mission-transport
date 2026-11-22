package com.example.mission.vehicule.dto;

import com.example.mission.vehicule.entity.EtatVehicule;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VehiculeResponse {
    private Long id;
    private String matricule;
    private String marque;
    private String modele;
    private Integer annee;
    private String type;
    private Long kilometrage;
    private EtatVehicule etat;

    public VehiculeResponse(Long id, String matricule, String marque, String modele, Long kilometrage, EtatVehicule etat , String type) {
        this.id = id;
        this.matricule = matricule;
        this.marque = marque;
        this.modele = modele;
        this.kilometrage = kilometrage;
        this.etat = etat;
        this.type = type;

    }
}
