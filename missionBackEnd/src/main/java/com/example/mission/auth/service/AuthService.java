package com.example.mission.auth.service;



import com.example.mission.auth.dto.LoginRequest;
import com.example.mission.auth.dto.LoginResponse;
import com.example.mission.shared.security.JwtService;
import com.example.mission.user.entity.Utilisateur;
import com.example.mission.user.repository.UtilisateurRepository;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UtilisateurRepository utilisateurRepository;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            UtilisateurRepository utilisateurRepository,
            JwtService jwtService) {

        this.authenticationManager = authenticationManager;
        this.utilisateurRepository = utilisateurRepository;
        this.jwtService = jwtService;
    }

    // Méthode de connexion
    public LoginResponse login(LoginRequest request) {

        // 1. Vérifier email et mot de passe
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getMotDePasse()
                )
        );

        // 2. Récupérer l'utilisateur
        Utilisateur utilisateur = utilisateurRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() ->
                        new RuntimeException("Utilisateur introuvable")
                );

        // 3. Générer le token JWT
        String token = jwtService.generateToken(utilisateur);

        // 4. Retourner les informations
        return new LoginResponse(
                token,
                utilisateur.getId(),
                utilisateur.getNom(),
                utilisateur.getPrenom(),
                utilisateur.getEmail(),
                utilisateur.getRole()
        );
    }

    public void logout() {

    }
}

