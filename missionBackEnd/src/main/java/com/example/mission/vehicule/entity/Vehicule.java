package com.example.mission.vehicule.entity;

import com.example.mission.shared.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@Table(name = "vehicule")
@Getter @Setter @NoArgsConstructor
public class Vehicule extends BaseEntity {

    @Column(nullable = false, unique = true, length = 30)
    private String matricule;
    private String marque;
    private String modele;
    private Integer annee;
    @Column(nullable = false)
    private Long kilometrage;
    private String type;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EtatVehicule etat = EtatVehicule.DISPONIBLE;

    @OneToMany(mappedBy = "vehicule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DocumentVehicule> documents = new ArrayList<>();

    //@OneToOne(mappedBy = "vehicule", fetch = FetchType.LAZY)
    //private CarteCarburant carteCarburant;
}
