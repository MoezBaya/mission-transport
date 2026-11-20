package com.example.mission.user.entity;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@DiscriminatorValue("CHAUFFEUR")
@Getter
@Setter
@NoArgsConstructor
public class Chauffeur extends Personnel {


    private String numPermis;
    private String categoriePermis;
    private LocalDate dateValiditePermis;
    private boolean disponible = true;

    @Override
    public Role getRole() {
        return Role.CHAUFFEUR;
    }

    public boolean getDisponible() {
        return disponible;
    }

}
