package com.example.mission.chauffeur.repository;

import com.example.mission.user.entity.Chauffeur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ChauffeurRepository extends JpaRepository<Chauffeur, Long> {

    /** Trouve un chauffeur par CIN */
    Optional<Chauffeur> findByCin(String cin);

    /** Trouve un chauffeur par email */
    Optional<Chauffeur> findByEmail(String email);

    /** Vérifie si un CIN existe déjà */
    boolean existsByCin(String cin);

    /** Vérifie si un email existe déjà */
    boolean existsByEmail(String email);

    /** Liste les chauffeurs disponibles */
    List<Chauffeur> findByDisponibleTrue();

    /** Liste les chauffeurs non disponibles */
    List<Chauffeur> findByDisponibleFalse();

    /** Recherche par nom ou prénom */
    List<Chauffeur> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            String nom, String prenom);
}
