package com.example.mission.user.repository;

import com.example.mission.user.entity.Utilisateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur , Long> {
    Optional<Utilisateur> findByEmail(String email);
    boolean existsByCin(String cin);
    boolean existsByEmail(String email);
}
