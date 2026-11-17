package com.example.mission.auth.dto;


import com.example.mission.user.entity.Role;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class LoginResponse {

    private String token;

    private Long id;

    private String nom;

    private String prenom;

    private String email;

    private Role role;
}
