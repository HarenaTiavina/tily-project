package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tily.mg.entity.Secteur;

import java.util.Optional;

@Repository
public interface SecteurRepository extends JpaRepository<Secteur, Integer> {
    Optional<Secteur> findByNom(String nom);
}

