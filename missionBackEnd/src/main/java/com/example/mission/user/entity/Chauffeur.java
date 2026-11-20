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
<<<<<<< HEAD
public class Chauffeur extends Utilisateur {

    private String numPermis;

    private String categoriePermis;

    private LocalDate dateValiditePermis;

=======
public class Chauffeur extends Personnel {

    private String numPermis;
    private String categoriePermis;
    private LocalDate dateValiditePermis;
>>>>>>> 213c397 (Second commit)
    private boolean disponible = true;

    @Override
    public Role getRole() {
        return Role.CHAUFFEUR;
    }
<<<<<<< HEAD
=======

    public Boolean getDisponible() {
        return disponible;
    }
>>>>>>> 213c397 (Second commit)
}
