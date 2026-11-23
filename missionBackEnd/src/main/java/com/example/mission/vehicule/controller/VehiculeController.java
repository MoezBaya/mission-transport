package com.example.mission.vehicule.controller;

import com.example.mission.vehicule.dto.VehiculeRequest;
import com.example.mission.vehicule.dto.VehiculeResponse;
import com.example.mission.vehicule.entity.EtatVehicule;
import com.example.mission.vehicule.service.VehiculeService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/vehicule")
@CrossOrigin(origins = "http://localhost:5173")
public class VehiculeController {

    private final VehiculeService vehiculeService;

    public VehiculeController(VehiculeService vehiculeService) {
        this.vehiculeService = vehiculeService;
    }

    @GetMapping
    public ResponseEntity<List<VehiculeResponse>> listerTous() {
        return ResponseEntity.ok(vehiculeService.listerTous());
    }

    @GetMapping("/{id}")
    public ResponseEntity<VehiculeResponse> trouverParId(@PathVariable Long id) {
        return ResponseEntity.ok(vehiculeService.trouverParId(id));
    }

    @PostMapping
    public ResponseEntity<VehiculeResponse> ajouter(@RequestBody VehiculeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehiculeService.ajouter(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<VehiculeResponse> modifier(@PathVariable Long id,
                                                     @RequestBody VehiculeRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(vehiculeService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimer(@PathVariable Long id) {
        vehiculeService.supprimer(id);
        return ResponseEntity.ok("Véhicule supprimé avec succès");
    }

    // Endpoint pour changer l'état
    @PatchMapping("/{id}/etat")
    public ResponseEntity<VehiculeResponse> changerEtat(@PathVariable Long id,
                                                        @RequestParam EtatVehicule etat) {
        return ResponseEntity.ok(vehiculeService.changerEtat(id, etat));
    }
}
