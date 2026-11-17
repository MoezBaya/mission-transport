package com.example.mission.user.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ModifierMdpRequest {

    private String ancienMotDePasse;
    private String nouveauMotDePasse;
    private String confirmationMotDePasse;
}
