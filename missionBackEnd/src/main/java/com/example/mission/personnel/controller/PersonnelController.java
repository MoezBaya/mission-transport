package com.example.mission.personnel.controller;

import com.example.mission.personnel.dto.PersonnelRequest;
import com.example.mission.personnel.dto.PersonnelResponse;
import com.example.mission.personnel.service.PersonnelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/api/personnel")
@CrossOrigin(origins = "http://localhost:5173")
public class PersonnelController {

    private final PersonnelService personnelService;

    public PersonnelController(PersonnelService personnelService) {
        this.personnelService = personnelService;
    }

    @GetMapping
    public ResponseEntity<List<PersonnelResponse>> listerTous() {
        return ResponseEntity.ok(personnelService.listerTous());
    }

    @PostMapping
    public ResponseEntity<PersonnelResponse> ajouter(@RequestBody PersonnelRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(personnelService.ajouter(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PersonnelResponse> modifier(@PathVariable Long id,
                                                      @RequestBody PersonnelRequest request) {
        return ResponseEntity.ok(personnelService.modifier(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> supprimer(@PathVariable Long id) {
        personnelService.supprimer(id);
        return ResponseEntity.ok("Personnel supprimé avec succès");
    }
}
