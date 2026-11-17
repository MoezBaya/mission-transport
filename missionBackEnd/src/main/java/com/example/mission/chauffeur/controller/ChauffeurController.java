package com.example.mission.chauffeur.controller;

import com.example.mission.chauffeur.dto.ChauffeurRequest;
import com.example.mission.chauffeur.dto.ChauffeurResponse;
import com.example.mission.chauffeur.service.ChauffeurService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/chauffeur")
@CrossOrigin(origins = "http://localhost:5173")
public class ChauffeurController {

    private final ChauffeurService chauffeurService;

    public ChauffeurController(ChauffeurService chauffeurService) {
        this.chauffeurService = chauffeurService;
    }

    // 1. LISTER — GET → 200 OK
    @GetMapping
    public ResponseEntity<List<ChauffeurResponse>> listerTous() {
        return ResponseEntity.ok(chauffeurService.listerTous());
    }

    // 2. AJOUTER — POST → 201 CREATED
    @PostMapping
    public ResponseEntity<ChauffeurResponse> ajouter(@RequestBody ChauffeurRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(chauffeurService.ajouter(request));
    }

    // 3. MODIFIER — PUT → 200 OK (ou 201 CREATED si ton prof l'exige)
    @PutMapping("/{id}")
    public ResponseEntity<ChauffeurResponse> modifier(@PathVariable Long id,
                                                      @RequestBody ChauffeurRequest request) {
        return ResponseEntity.ok(chauffeurService.modifier(id, request));
    }

    // 4. SUPPRIMER — DELETE → 200 OK
    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimer(@PathVariable Long id) {
        chauffeurService.supprimer(id);
        return ResponseEntity.ok("Chauffeur supprimé avec succès");
    }
}
