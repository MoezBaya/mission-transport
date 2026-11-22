package com.example.mission.personnel.service;

import com.example.mission.personnel.dto.PersonnelRequest;
import com.example.mission.personnel.dto.PersonnelResponse;
import com.example.mission.personnel.repository.PersonnelRepository;
import com.example.mission.shared.security.SecurityService;
import com.example.mission.shared.validator.UtilisateurValidator;
import com.example.mission.user.entity.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonnelService {

    private final PersonnelRepository personnelRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final UtilisateurValidator utilisateurValidator;


    public PersonnelService(PersonnelRepository personnelRepository,
                            PasswordEncoder passwordEncoder, SecurityService securityService, UtilisateurValidator utilisateurValidator) {
        this.personnelRepository = personnelRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
        this.utilisateurValidator = utilisateurValidator;
    }

    // ============================================================
    // 1. LISTER
    // ============================================================
    @Transactional(readOnly = true)
    public List<PersonnelResponse> listerTous() {
        securityService.verifierAuthentifie();
        return personnelRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ============================================================
    // 2. TROUVER PAR ID
    // ============================================================
    @Transactional(readOnly = true)
    public PersonnelResponse trouverParId(Long id) {
        securityService.verifierAuthentifie();
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable: " + id));
        return toResponse(personnel);
    }

    // ============================================================
    // 3. AJOUTER
    // ============================================================

    @Transactional
    public PersonnelResponse ajouter(PersonnelRequest request) {
        securityService.verifierRole("DIRECTEUR");

        // ⚡ Validation centralisée
        utilisateurValidator.validerChampsObligatoires(
                request.getNom(), request.getPrenom(),
                request.getCin(), request.getEmail(),
                request.getMotDePasse()
        );

        // Validation spécifique Personnel
        if (request.getRole() == null || request.getRole().isBlank())
            throw new IllegalArgumentException("Le rôle est obligatoire");

        // ⚡ Unicité centralisée
        utilisateurValidator.verifierUnicite(request.getCin(), request.getEmail());

        Personnel personnel = creerSelonRole(request.getRole());
        copierChamps(request, personnel, true);
        return toResponse(personnelRepository.save(personnel));
    }

    @Transactional
    public PersonnelResponse modifier(Long id, PersonnelRequest request) {
        securityService.verifierRole("DIRECTEUR");

        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable: " + id));

        // ⚡ Validation centralisée
        utilisateurValidator.validerChampsModification(
                request.getNom(), request.getPrenom()
        );

        // ⚡ Unicité centralisée
        utilisateurValidator.verifierUniciteModification(
                personnel.getCin(), personnel.getEmail(),
                request.getCin(), request.getEmail()
        );

        copierChamps(request, personnel, false);
        return toResponse(personnelRepository.save(personnel));
    }


    @Transactional
    public void supprimer(Long id) {
        securityService.verifierRole("DIRECTEUR");

        if (!personnelRepository.existsById(id))
            throw new IllegalArgumentException("Personnel introuvable: " + id);

        personnelRepository.deleteById(id);
    }

    // ============================================================
    // HELPERS
    // ============================================================
    private Personnel creerSelonRole(String roleStr) {
        Role role;
        try {
            role = Role.valueOf(roleStr.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Rôle invalide: " + roleStr);
        }

        return switch (role) {
            case CHEF_DE_PARC -> new ChefDeParc();
            case DIRECTEUR -> new Directeur();
            case DIRECTEUR_LOGISTIQUE -> new DirecteurLogistique();
            case CHAUFFEUR -> throw new IllegalArgumentException(
                    "CHAUFFEUR n'est pas un Personnel. Utilisez /v1/api/chauffeur");
        };
    }

    private void copierChamps(PersonnelRequest request, Personnel personnel, boolean creation) {
        personnel.setNom(request.getNom());
        personnel.setPrenom(request.getPrenom());
        personnel.setCin(request.getCin());
        personnel.setEmail(request.getEmail());
        personnel.setTelephone(request.getTelephone());

        if (creation || (request.getMotDePasse() != null && !request.getMotDePasse().isBlank())) {
            personnel.setMotDePasse(passwordEncoder.encode(request.getMotDePasse()));
        }
    }

    private PersonnelResponse toResponse(Personnel p) {
        return new PersonnelResponse(
                p.getId(),
                p.getNom(),
                p.getPrenom(),
                p.getCin(),
                p.getEmail(),
                p.getTelephone(),
                p.getRole().name()
        );
    }
}
