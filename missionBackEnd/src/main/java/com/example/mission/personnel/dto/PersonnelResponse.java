package com.example.mission.personnel.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PersonnelResponse {
    private Long id;           // ← premier champ
    private String nom;
    private String prenom;
    private String cin;
    private String email;
    private String telephone;
    private String role;
}
