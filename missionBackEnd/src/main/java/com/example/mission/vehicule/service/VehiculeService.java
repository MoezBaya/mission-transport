package com.example.mission.vehicule.service;

import com.example.mission.shared.security.SecurityService;
import com.example.mission.vehicule.dto.VehiculeRequest;
import com.example.mission.vehicule.dto.VehiculeResponse;
import com.example.mission.vehicule.entity.EtatVehicule;
import com.example.mission.vehicule.entity.Vehicule;
import com.example.mission.vehicule.repository.VehiculeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehiculeService {

    private final VehiculeRepository vehiculeRepository;
    private final SecurityService securityService;

    public VehiculeService(VehiculeRepository vehiculeRepository, SecurityService securityService) {
        this.vehiculeRepository = vehiculeRepository;
        this.securityService = securityService;
    }

    // 1. LISTER
    @Transactional(readOnly = true)
    public List<VehiculeResponse> listerTous() {
        securityService.verifierAuthentifie();
        return vehiculeRepository.findAll()
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // 2. TROUVER PAR ID
    @Transactional(readOnly = true)
    public VehiculeResponse trouverParId(Long id) {
        securityService.verifierAuthentifie();
        Vehicule v = vehiculeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule introuvable: " + id));
        return toResponse(v);
    }

    // 3. AJOUTER
    @Transactional
    public VehiculeResponse ajouter(VehiculeRequest request) {
        securityService.verifierRole("DIRECTEUR");

        if (request.getMatricule() == null || request.getMatricule().isBlank())
            throw new IllegalArgumentException("Le matricule est obligatoire");
        if (request.getMarque() == null || request.getMarque().isBlank())
            throw new IllegalArgumentException("La marque est obligatoire");
        if (request.getModele() == null || request.getModele().isBlank())
            throw new IllegalArgumentException("Le modèle est obligatoire");

        if (vehiculeRepository.existsByMatricule(request.getMatricule()))
            throw new IllegalArgumentException("Un véhicule avec ce matricule existe déjà");

        Vehicule vehicule = new Vehicule();
        copierChamps(request, vehicule);
        return toResponse(vehiculeRepository.save(vehicule));
    }

    // 4. MODIFIER
    @Transactional
    public VehiculeResponse modifier(Long id, VehiculeRequest request) {
        securityService.verifierRole("DIRECTEUR");

        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule introuvable: " + id));

        if (!vehicule.getMatricule().equals(request.getMatricule())
                && vehiculeRepository.existsByMatricule(request.getMatricule()))
            throw new IllegalArgumentException("Un véhicule avec ce matricule existe déjà");

        copierChamps(request, vehicule);
        return toResponse(vehiculeRepository.save(vehicule));
    }

    // 5. SUPPRIMER
    @Transactional
    public void supprimer(Long id) {
        securityService.verifierRole("DIRECTEUR");

        if (!vehiculeRepository.existsById(id))
            throw new IllegalArgumentException("Véhicule introuvable: " + id);

        vehiculeRepository.deleteById(id);
    }

    // 6. CHANGER L'ÉTAT
    @Transactional
    public VehiculeResponse changerEtat(Long id, EtatVehicule nouvelEtat) {
        securityService.verifierRole("CHEF_DE_PARC");

        Vehicule vehicule = vehiculeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Véhicule introuvable: " + id));

        vehicule.setEtat(nouvelEtat);
        return toResponse(vehiculeRepository.save(vehicule));
    }
    // ============================================================
    // HELPERS
    // ============================================================
    private void copierChamps(VehiculeRequest r, Vehicule v) {
        v.setMatricule(r.getMatricule());
        v.setMarque(r.getMarque());
        v.setModele(r.getModele());
        v.setKilometrage(r.getKilometrage());
        v.setType(r.getType());
        v.setEtat(r.getEtat() != null ? r.getEtat() : EtatVehicule.DISPONIBLE);
    }

    private VehiculeResponse toResponse(Vehicule v) {
        return new VehiculeResponse(
                v.getId(),
                v.getMatricule(),
                v.getMarque(),
                v.getModele(),
                v.getKilometrage(),
                v.getEtat(),
                v.getType()
        );
    }
}