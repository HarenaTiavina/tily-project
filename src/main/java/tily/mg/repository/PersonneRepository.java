package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tily.mg.entity.Personne;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonneRepository extends JpaRepository<Personne, Integer> {
    
    // Find by type
    @Query("SELECT p FROM Personne p WHERE p.typePersonne.nom = :typeName")
    List<Personne> findByTypePersonneNom(@Param("typeName") String typeName);
    
    // Find by id with Fafi loaded
    @Query("SELECT p FROM Personne p LEFT JOIN FETCH p.fafi WHERE p.id = :id")
    Optional<Personne> findByIdWithFafi(@Param("id") Integer id);
    
    // Find responsables
    @Query("SELECT p FROM Personne p LEFT JOIN FETCH p.fafi WHERE p.typePersonne.nom = 'Responsable'")
    List<Personne> findAllResponsables();
    
    // Find eleves
    @Query("SELECT p FROM Personne p LEFT JOIN FETCH p.fafi WHERE p.typePersonne.nom = 'Eleve'")
    List<Personne> findAllEleves();
    
    // Find by secteur
    List<Personne> findBySecteurId(Integer secteurId);
    
    // Find by fizarana
    List<Personne> findByFizaranaId(Integer fizaranaId);
    
    // Find by ambaratonga
    List<Personne> findByAmbaratonga(String ambaratonga);
    
    // Count by type
    @Query("SELECT COUNT(p) FROM Personne p WHERE p.typePersonne.nom = :typeName")
    Long countByTypePersonneNom(@Param("typeName") String typeName);
    
    // Count responsables with fafi
    @Query("SELECT COUNT(p) FROM Personne p WHERE p.typePersonne.nom = 'Responsable' AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active'")
    Long countResponsablesWithFafi();
    
    // Count eleves with fafi
    @Query("SELECT COUNT(p) FROM Personne p WHERE p.typePersonne.nom = 'Eleve' AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active'")
    Long countElevesWithFafi();
    
    // Search
    @Query("SELECT p FROM Personne p WHERE " +
           "LOWER(p.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.totem) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Personne> search(@Param("search") String search);
    
    // Filter responsables (pas de niveau, utilise andraikitra au lieu de section)
    @Query("SELECT DISTINCT p FROM Personne p LEFT JOIN FETCH p.fafi WHERE p.typePersonne.nom = 'Responsable' " +
           "AND (:secteurId IS NULL OR p.secteur.id = :secteurId) " +
           "AND (:andraikitraId IS NULL OR p.andraikitra.id = :andraikitraId) " +
           "AND (:hasFafi IS NULL OR " +
           "(:hasFafi = true AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active') OR " +
           "(:hasFafi = false AND (p.fafi IS NULL OR p.fafi.statut != 'Active')))")
    List<Personne> filterResponsables(
        @Param("secteurId") Integer secteurId,
        @Param("andraikitraId") Integer andraikitraId,
        @Param("hasFafi") Boolean hasFafi
    );
    
    // Filter eleves (Beazina)
    @Query("SELECT DISTINCT p FROM Personne p LEFT JOIN FETCH p.fafi WHERE p.typePersonne.nom = 'Eleve' " +
           "AND (:secteurId IS NULL OR p.secteur.id = :secteurId) " +
           "AND (:fizaranaId IS NULL OR p.fizarana.id = :fizaranaId) " +
           "AND (:ambaratonga IS NULL OR p.ambaratonga = :ambaratonga) " +
           "AND (:hasFafi IS NULL OR " +
           "(:hasFafi = true AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active') OR " +
           "(:hasFafi = false AND (p.fafi IS NULL OR p.fafi.statut != 'Active')))")
    List<Personne> filterEleves(
        @Param("secteurId") Integer secteurId,
        @Param("fizaranaId") Integer fizaranaId,
        @Param("ambaratonga") String ambaratonga,
        @Param("hasFafi") Boolean hasFafi
    );
}

