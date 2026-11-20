package com.example.mission.personnel.service;

import com.example.mission.personnel.dto.PersonnelRequest;
import com.example.mission.personnel.dto.PersonnelResponse;
import com.example.mission.personnel.repository.PersonnelRepository;
import com.example.mission.user.entity.*;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PersonnelService {

    private final PersonnelRepository personnelRepository;
    private final PasswordEncoder passwordEncoder;

    public PersonnelService(PersonnelRepository personnelRepository,
                            PasswordEncoder passwordEncoder) {
        this.personnelRepository = personnelRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // ============================================================
    // 1. LISTER
    // ============================================================
    @Transactional(readOnly = true)
    public List<PersonnelResponse> listerTous() {
        verifierAuthentifie();
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
        verifierAuthentifie();
        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable: " + id));
        return toResponse(personnel);
    }

    // ============================================================
    // 3. AJOUTER
    // ============================================================
    @Transactional
    public PersonnelResponse ajouter(PersonnelRequest request) {
        verifierRole("DIRECTEUR", "DIRECTEUR_LOGISTIQUE");

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
        if (request.getRole() == null || request.getRole().isBlank())
            throw new IllegalArgumentException("Le rôle est obligatoire");

        // unicité
        if (personnelRepository.existsByCin(request.getCin()))
            throw new IllegalArgumentException("Un personnel avec ce CIN existe déjà");
        if (personnelRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Un personnel avec cet email existe déjà");

        Personnel personnel = creerSelonRole(request.getRole());
        copierChamps(request, personnel, true);
        return toResponse(personnelRepository.save(personnel));
    }

    // ============================================================
    // 4. MODIFIER
    // ============================================================
    @Transactional
    public PersonnelResponse modifier(Long id, PersonnelRequest request) {
        verifierRole("DIRECTEUR", "DIRECTEUR_LOGISTIQUE");

        Personnel personnel = personnelRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Personnel introuvable: " + id));

        if (request.getNom() == null || request.getNom().isBlank())
            throw new IllegalArgumentException("Le nom est obligatoire");
        if (request.getPrenom() == null || request.getPrenom().isBlank())
            throw new IllegalArgumentException("Le prénom est obligatoire");

        if (!personnel.getCin().equals(request.getCin())
                && personnelRepository.existsByCin(request.getCin()))
            throw new IllegalArgumentException("Un personnel avec ce CIN existe déjà");

        if (!personnel.getEmail().equals(request.getEmail())
                && personnelRepository.existsByEmail(request.getEmail()))
            throw new IllegalArgumentException("Un personnel avec cet email existe déjà");

        copierChamps(request, personnel, false);
        return toResponse(personnelRepository.save(personnel));
    }

    // ============================================================
    // 5. SUPPRIMER
    // ============================================================
    @Transactional
    public void supprimer(Long id) {
        verifierRole("DIRECTEUR");

        if (!personnelRepository.existsById(id))
            throw new IllegalArgumentException("Personnel introuvable: " + id);

        personnelRepository.deleteById(id);
    }

    // ============================================================
    // SÉCURITÉ
    // ============================================================
    private void verifierAuthentifie() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null
                || !auth.isAuthenticated()
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
                p.getId(),           // ⚡ AJOUTÉ
                p.getNom(),
                p.getPrenom(),
                p.getCin(),
                p.getEmail(),
                p.getTelephone(),
                p.getRole().name()
        );
    }
}
