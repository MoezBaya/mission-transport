package com.example.mission.personnel.repository;

import com.example.mission.user.entity.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    // ============================================================
    // RECHERCHE PAR CHAMP UNIQUE
    // ============================================================

    Optional<Personnel> findByCin(String cin);

    Optional<Personnel> findByEmail(String email);

    // ============================================================
    // VÉRIFICATION D'UNICITÉ
    // ============================================================

    boolean existsByCin(String cin);

    boolean existsByEmail(String email);

    // ============================================================
    // RECHERCHE PAR RÔLE (via la colonne discriminatrice)
    // ============================================================
    // ⚠️ On utilise une @Query native car "role" est le DISCRIMINATOR,
    // pas un champ Java classique (getRole() est @Transient).

    @Query("SELECT p FROM Personnel p WHERE TYPE(p) = :type")
    List<Personnel> findByType(@Param("type") Class<? extends Personnel> type);

    // ============================================================
    // RECHERCHE PAR NOM / PRÉNOM (utile pour la recherche)
    // ============================================================

    List<Personnel> findByNomContainingIgnoreCase(String nom);

    List<Personnel> findByPrenomContainingIgnoreCase(String prenom);

    List<Personnel> findByNomContainingIgnoreCaseOrPrenomContainingIgnoreCase(
            String nom, String prenom);
}
