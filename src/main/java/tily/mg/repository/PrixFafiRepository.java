package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tily.mg.entity.PrixFafi;

import java.util.List;
import java.util.Optional;

@Repository
public interface PrixFafiRepository extends JpaRepository<PrixFafi, Integer> {
    
    /**
     * Trouve le prix actif pour un type de personne et une année donnée
     */
    Optional<PrixFafi> findByTypePersonneAndAnneeAndActifTrue(String typePersonne, Integer annee);
    
    /**
     * Trouve tous les prix pour une année donnée
     */
    List<PrixFafi> findByAnneeAndActifTrue(Integer annee);
    
    /**
     * Trouve tous les prix pour un type de personne
     */
    List<PrixFafi> findByTypePersonneOrderByAnneeDesc(String typePersonne);
    
    /**
     * Trouve le prix actif pour les Mpiandraikitra pour l'année courante
     */
    @Query("SELECT p FROM PrixFafi p WHERE p.typePersonne = 'Mpiandraikitra' AND p.annee = :annee AND p.actif = true")
    Optional<PrixFafi> findPrixMpiandraikitraForYear(@Param("annee") Integer annee);
    
    /**
     * Trouve le prix actif pour les Beazina pour l'année courante
     */
    @Query("SELECT p FROM PrixFafi p WHERE p.typePersonne = 'Beazina' AND p.annee = :annee AND p.actif = true")
    Optional<PrixFafi> findPrixBeazinaForYear(@Param("annee") Integer annee);
    
    /**
     * Trouve tous les prix avec leur historique
     */
    @Query("SELECT p FROM PrixFafi p ORDER BY p.annee DESC, p.typePersonne")
    List<PrixFafi> findAllOrderByAnneeDesc();
    
    /**
     * Vérifie si un prix existe pour un type et une année
     */
    boolean existsByTypePersonneAndAnnee(String typePersonne, Integer annee);
}

