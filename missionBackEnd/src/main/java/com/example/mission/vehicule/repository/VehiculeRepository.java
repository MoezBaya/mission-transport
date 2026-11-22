package com.example.mission.vehicule.repository;

import com.example.mission.vehicule.entity.EtatVehicule;
import com.example.mission.vehicule.entity.Vehicule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VehiculeRepository extends JpaRepository<Vehicule, Long> {

    Optional<Vehicule> findByMatricule(String matricule);

    boolean existsByMatricule(String matricule);

    List<Vehicule> findByEtat(EtatVehicule etat);

    List<Vehicule> findByMarqueContainingIgnoreCase(String marque);

    List<Vehicule> findByModeleContainingIgnoreCase(String modele);
}