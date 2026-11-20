package com.example.mission.chauffeur.service;

import com.example.mission.chauffeur.dto.ChauffeurRequest;
import com.example.mission.chauffeur.dto.ChauffeurResponse;
import com.example.mission.chauffeur.repository.ChauffeurRepository;
import com.example.mission.user.entity.Chauffeur;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChauffeurService {

    private final ChauffeurRepository chauffeurRepository;
    private final PasswordEncoder passwordEncoder;

    public ChauffeurService(ChauffeurRepository chauffeurRepository,
                            PasswordEncoder passwordEncoder) {
        this.chauffeurRepository = chauffeurRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================================================
    // 1. LISTER — tous les rôles authentifiés
    // ============================================================
    @Transactional(readOnly = true)
    public List<ChauffeurResponse> listerTous() {
        verifierAuthentifie();
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
        verifierAuthentifie();
        Chauffeur c = chauffeurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chauffeur introuvable: " + id));
        return toResponse(c);
    }

    // ============================================================
    // 3. AJOUTER — ChefParc + Directeur + DirecteurLogistique
    // ============================================================
    @Transactional
    public ChauffeurResponse ajouter(ChauffeurRequest request) {
        verifierRole("DIRECTEUR");

        // validations
        if (request.getNom() == null || request.getNom().isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire");
        if (request.getPrenom() == null || request.getPrenom().isBlank())
            throw new IllegalArgumentException("Le prénom est obligatoire");
        if (request.getCin() == null || request.getCin().isBlank())
            throw new IllegalArgumentException("Le CIN est obligatoire");
        if (request.getEmail() == null || request.getEmail().isBlank())
            throw new IllegalArgumentException("L'email est obligatoire");
        if (request.getMotDePasse() == null || request.getMotDePasse().length() < 6)
            throw new IllegalArgumentException("Le mot de passe doit contenir au moins 6 caractères");
        if (request.getNumeroPermis() == null || request.getNumeroPermis().isBlank())
            throw new IllegalArgumentException("Le numéro de permis est obligatoire");

        // unicité
        if (chauffeurRepository.existsByCin(request.getCin()))
            throw new IllegalArgumentException("Un chauffeur avec ce CIN existe déjà");
        if (chauffeurRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Un chauffeur avec cet email existe déjà");

        Chauffeur chauffeur = new Chauffeur();
        copierChamps(request, chauffeur, true);
        return toResponse(chauffeurRepository.save(chauffeur));
    }

    // ============================================================
    // 4. MODIFIER — SEULEMENT Directeur ⚡
    // ============================================================
    @Transactional
    public ChauffeurResponse modifier(Long id, ChauffeurRequest request) {
        verifierRole("DIRECTEUR");   // ⚡ Directeur seulement

        Chauffeur chauffeur = chauffeurRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Chauffeur introuvable: " + id));

        if (request.getNom() == null || request.getNom().isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire");
        if (request.getPrenom() == null || request.getPrenom().isBlank())
            throw new IllegalArgumentException("Le prénom est obligatoire");

        // unicité (si changé)
        if (!chauffeur.getCin().equals(request.getCin())
                && chauffeurRepository.existsByCin(request.getCin()))
            throw new IllegalArgumentException("Un chauffeur avec ce CIN existe déjà");

        if (!chauffeur.getEmail().equals(request.getEmail())
                && chauffeurRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Un chauffeur avec cet email existe déjà");

        copierChamps(request, chauffeur, false);
        return toResponse(chauffeurRepository.save(chauffeur));
    }

    // ============================================================
    // 5. SUPPRIMER — SEULEMENT Directeur ⚡
    // ============================================================
    @Transactional
    public void supprimer(Long id) {
        verifierRole("DIRECTEUR");   // ⚡ Directeur seulement

        if (!chauffeurRepository.existsById(id))
            throw new IllegalArgumentException("Chauffeur introuvable: " + id);

        chauffeurRepository.deleteById(id);
    }

    // ============================================================
    // SÉCURITÉ
    // ============================================================
    private void verifierAuthentifie() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()
                || "anonymousUser".equals(auth.getPrincipal())) {
            throw new AccessDeniedException("Vous devez être authentifié");
        }
    }

    private void verifierRole(String... rolesAutorises) {
        verifierAuthentifie();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean autorise = auth.getAuthorities().stream()
                .anyMatch(a -> {
                    for (String role : rolesAutorises) {
                        if (a.getAuthority().equals("ROLE_" + role)) return true;
                    }
                    return false;
                });
        if (!autorise) {
            throw new AccessDeniedException(
                    "Accès refusé. Rôles autorisés: " + String.join(", ", rolesAutorises));
        }
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
