package com.example.mission.user.entity;

import com.example.mission.user.entity.Role;
import com.example.mission.user.entity.Utilisateur;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.time.*;
import java.math.BigDecimal;
import java.util.*;

@Entity
@DiscriminatorValue("DIRECTEUR")
@NoArgsConstructor
public class Directeur extends Personnel {

    @Override
    public Role getRole() {
        return Role.DIRECTEUR;
    }
}
