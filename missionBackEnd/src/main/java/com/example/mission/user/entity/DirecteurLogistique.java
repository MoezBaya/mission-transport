package com.example.mission.user.entity;


import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.NoArgsConstructor;

@Entity
@DiscriminatorValue("DIRECTEUR_LOGISTIQUE")
@NoArgsConstructor
public class DirecteurLogistique extends Utilisateur {

    @Override
    public Role getRole() {
        return Role.DIRECTEUR_LOGISTIQUE;
    }
}
