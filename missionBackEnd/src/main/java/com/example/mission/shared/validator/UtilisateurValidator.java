package com.example.mission.shared.validator;

import com.example.mission.user.repository.UtilisateurRepository;
import org.springframework.stereotype.Component;

@Component
public class UtilisateurValidator {

    private final UtilisateurRepository utilisateurRepository;

    public UtilisateurValidator(UtilisateurRepository utilisateurRepository) {
        this.utilisateurRepository = utilisateurRepository;
    }

    // ============================================================
    // 1. VALIDATION CRÉATION (tous les champs obligatoires)
    // ============================================================
    public void validerChampsObligatoires(String nom, String prenom,
                                          String cin, String email,
                                          String motDePasse) {
        if (nom == null || nom.isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire");
        if (prenom == null || prenom.isBlank())
            throw new IllegalArgumentException("Le prénom est obligatoire");
        if (cin == null || cin.isBlank())
            throw new IllegalArgumentException("Le CIN est obligatoire");
        if (email == null || email.isBlank())
            throw new IllegalArgumentException("L'email est obligatoire");
        if (motDePasse == null || motDePasse.length() < 6)
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
    }

    // ============================================================
    // 2. VALIDATION MODIFICATION (nom + prénom obligatoires)
    // ============================================================
    public void validerChampsModification(String nom, String prenom) {
        if (nom == null || nom.isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire");
        if (prenom == null || prenom.isBlank())
            throw new IllegalArgumentException("Le prénom est obligatoire");
    }

    // ============================================================
    // 3. UNICITÉ (création)
    // ============================================================
    public void verifierUnicite(String cin, String email) {
        if (utilisateurRepository.existsByCin(cin))
            throw new IllegalArgumentException("Un utilisateur avec ce CIN existe déjà");
        if (utilisateurRepository.existsByEmail(email))
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
    }

    // ============================================================
    // 4. UNICITÉ (modification — si changé)
    // ============================================================
    public void verifierUniciteModification(String ancienCin, String ancienEmail,
                                            String nouveauCin, String nouveauEmail) {
        if (!ancienCin.equals(nouveauCin) && utilisateurRepository.existsByCin(nouveauCin))
            throw new IllegalArgumentException("Un utilisateur avec ce CIN existe déjà");
        if (!ancienEmail.equals(nouveauEmail) && utilisateurRepository.existsByEmail(nouveauEmail))
            throw new IllegalArgumentException("Un utilisateur avec cet email existe déjà");
    }
}
