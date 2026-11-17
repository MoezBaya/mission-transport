package com.example.mission.user.entity;

import jakarta.persistence.*;

import lombok.NoArgsConstructor;


@Entity
@DiscriminatorValue("CHEF_DE_PARC")
@NoArgsConstructor
public class ChefDeParc extends Utilisateur {

    @Override
    public Role getRole() {
        return Role.CHEF_DE_PARC;
    }
}
