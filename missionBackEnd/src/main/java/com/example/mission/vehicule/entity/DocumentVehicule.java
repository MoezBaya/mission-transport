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
@Table(name = "document_vehicule")
@Getter @Setter @NoArgsConstructor
public class DocumentVehicule extends BaseEntity {

    @Column(nullable = false)
    private String nom;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TypeDocument type;

    @Column(nullable = false)
    private LocalDate dateExpiration;

    private String urlImage;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "vehicule_id")
    private Vehicule vehicule;

    public boolean estExpire() {
        return dateExpiration.isBefore(LocalDate.now());
    }
}
