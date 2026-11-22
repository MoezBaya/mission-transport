package com.example.mission.chauffeur.service;

import com.example.mission.chauffeur.dto.ChauffeurRequest;
import com.example.mission.chauffeur.dto.ChauffeurResponse;
import com.example.mission.chauffeur.repository.ChauffeurRepository;
import com.example.mission.shared.security.SecurityService;
import com.example.mission.shared.validator.UtilisateurValidator;
import com.example.mission.user.entity.Chauffeur;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChauffeurService {

    private final ChauffeurRepository chauffeurRepository;
    private final PasswordEncoder passwordEncoder;
    private final SecurityService securityService;
    private final UtilisateurValidator utilisateurValidator;   // ⚡

    public ChauffeurService(ChauffeurRepository chauffeurRepository,
                            PasswordEncoder passwordEncoder,
                            SecurityService securityService,
                            UtilisateurValidator utilisateurValidator) {
        this.chauffeurRepository = chauffeurRepository;
        this.passwordEncoder = passwordEncoder;
        this.securityService = securityService;
        this.utilisateurValidator = utilisateurValidator;
    }


    // ============================================================
    // 1. LISTER — tous les rôles authentifiés
    // ============================================================
    @Transactional(readOnly = true)
    public List<ChauffeurResponse> listerTous() {
        securityService.verifierAuthentifie();
        return chauffeurRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ============================================================
    // 2. TROUVER PAR ID — tous les rôles authentifiés
    // ============================================================
    @Transactional(readOnly = true)
    public ChauffeurResponse trouverParId(Long id) {
        securityService.verifierAuthentifie();
        Chauffeur c = chauffeurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chauffeur introuvable: " + id));
        return toResponse(c);
    }


        @Transactional
        public ChauffeurResponse ajouter(ChauffeurRequest request) {
            securityService.verifierRole("DIRECTEUR");

            // ⚡ Validation centralisée
            utilisateurValidator.validerChampsObligatoires(
                    request.getNom(), request.getPrenom(),
                    request.getCin(), request.getEmail(),
                    request.getMotDePasse()
            );

            // Validation spécifique Chauffeur
            if (request.getNumeroPermis() == null || request.getNumeroPermis().isBlank())
                throw new IllegalArgumentException("Le numéro de permis est obligatoire");

            // ⚡ Unicité centralisée
            utilisateurValidator.verifierUnicite(request.getCin(), request.getEmail());

            Chauffeur chauffeur = new Chauffeur();
            copierChamps(request, chauffeur, true);
            return toResponse(chauffeurRepository.save(chauffeur));
        }

    @Transactional
    public ChauffeurResponse modifier(Long id, ChauffeurRequest request) {
        securityService.verifierRole("DIRECTEUR");

        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chauffeur introuvable: " + id));

        // ⚡ Validation centralisée
        utilisateurValidator.validerChampsModification(
                request.getNom(), request.getPrenom()
        );

        // ⚡ Unicité centralisée
        utilisateurValidator.verifierUniciteModification(
                chauffeur.getCin(), chauffeur.getEmail(),
                request.getCin(), request.getEmail()
        );

        copierChamps(request, chauffeur, false);
        return toResponse(chauffeurRepository.save(chauffeur));
    }

    // ============================================================
    // 5. SUPPRIMER — SEULEMENT Directeur ⚡
    // ============================================================
    @Transactional
    public void supprimer(Long id) {
        securityService.verifierRole("DIRECTEUR");   // ⚡ Directeur seulement

        if (!chauffeurRepository.existsById(id))
            throw new IllegalArgumentException("Chauffeur introuvable: " + id);

        chauffeurRepository.deleteById(id);
    }


    // ============================================================
    // HELPERS
    // ============================================================
    private void copierChamps(ChauffeurRequest r, Chauffeur c, boolean creation) {
        c.setNom(r.getNom());
        c.setPrenom(r.getPrenom());
        c.setCin(r.getCin());
        c.setEmail(r.getEmail());
        c.setTelephone(r.getTelephone());
        c.setNumPermis(r.getNumeroPermis());
        c.setCategoriePermis(r.getCategoriePermis());
        c.setDateValiditePermis(r.getDateValiditePermis());
        c.setDisponible(r.getDisponible() != null ? r.getDisponible() : true);

        if (creation || (r.getMotDePasse() != null && !r.getMotDePasse().isBlank())) {
            c.setMotDePasse(passwordEncoder.encode(r.getMotDePasse()));
        }
    }

    private ChauffeurResponse toResponse(Chauffeur c) {
        return new ChauffeurResponse(
                c.getId(),
                c.getNom(),
                c.getPrenom(),
                c.getCin(),
                c.getEmail(),
                c.getTelephone(),
                c.getNumPermis(),
                c.getCategoriePermis(),
                c.getDateValiditePermis(),
                c.getDisponible()
        );
    }
}
