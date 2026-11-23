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
}
