package com.example.mission.vehicule.dto;

import com.example.mission.vehicule.entity.EtatVehicule;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class VehiculeRequest {
    private String matricule;
    private String marque;
    private String modele;

    private Integer annee;
    private String type;
    private Integer nombrePlaces;
    private Boolean disponible;
    private Long kilometrage;
    private EtatVehicule etat;
}
