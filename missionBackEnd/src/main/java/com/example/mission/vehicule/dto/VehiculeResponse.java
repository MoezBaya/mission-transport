package com.example.mission.vehicule.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class VehiculeResponse {
    private Long id;
    private String immatriculation;
    private String marque;
    private String modele;
    private Integer annee;
    private String type;
    private Double capaciteCharge;
    private Integer nombrePlaces;
    private Boolean disponible;
    private String etat;
}
