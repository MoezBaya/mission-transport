package com.example.mission.vehicule.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiculeRequest {
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
