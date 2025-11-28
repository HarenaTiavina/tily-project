package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tily.mg.entity.Personne;

import java.util.List;

@Repository
public interface PersonneRepository extends JpaRepository<Personne, Integer> {
    
    // Find by type
    @Query("SELECT p FROM Personne p WHERE p.typePersonne.nom = :typeName")
    List<Personne> findByTypePersonneNom(@Param("typeName") String typeName);
    
    // Find responsables
    @Query("SELECT p FROM Personne p WHERE p.typePersonne.nom = 'Responsable'")
    List<Personne> findAllResponsables();
    
    // Find eleves
    @Query("SELECT p FROM Personne p WHERE p.typePersonne.nom = 'Eleve'")
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
    
    // Count responsables with assurance
    @Query("SELECT COUNT(p) FROM Personne p WHERE p.typePersonne.nom = 'Responsable' AND p.assurance IS NOT NULL AND p.assurance.statut = 'Active'")
    Long countResponsablesWithAssurance();
    
    // Count eleves with assurance
    @Query("SELECT COUNT(p) FROM Personne p WHERE p.typePersonne.nom = 'Eleve' AND p.assurance IS NOT NULL AND p.assurance.statut = 'Active'")
    Long countElevesWithAssurance();
    
    // Search
    @Query("SELECT p FROM Personne p WHERE " +
           "LOWER(p.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.totem) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Personne> search(@Param("search") String search);
    
    // Filter responsables (pas de niveau, utilise andraikitra au lieu de section)
    @Query("SELECT p FROM Personne p WHERE p.typePersonne.nom = 'Responsable' " +
           "AND (:secteurId IS NULL OR p.secteur.id = :secteurId) " +
           "AND (:andraikitraId IS NULL OR p.andraikitra.id = :andraikitraId) " +
           "AND (:hasAssurance IS NULL OR " +
           "(:hasAssurance = true AND p.assurance IS NOT NULL AND p.assurance.statut = 'Active') OR " +
           "(:hasAssurance = false AND (p.assurance IS NULL OR p.assurance.statut != 'Active')))")
    List<Personne> filterResponsables(
        @Param("secteurId") Integer secteurId,
        @Param("andraikitraId") Integer andraikitraId,
        @Param("hasAssurance") Boolean hasAssurance
    );
    
    // Filter eleves (Beazina)
    @Query("SELECT p FROM Personne p WHERE p.typePersonne.nom = 'Eleve' " +
           "AND (:secteurId IS NULL OR p.secteur.id = :secteurId) " +
           "AND (:fizaranaId IS NULL OR p.fizarana.id = :fizaranaId) " +
           "AND (:ambaratonga IS NULL OR p.ambaratonga = :ambaratonga) " +
           "AND (:hasAssurance IS NULL OR " +
           "(:hasAssurance = true AND p.assurance IS NOT NULL AND p.assurance.statut = 'Active') OR " +
           "(:hasAssurance = false AND (p.assurance IS NULL OR p.assurance.statut != 'Active')))")
    List<Personne> filterEleves(
        @Param("secteurId") Integer secteurId,
        @Param("fizaranaId") Integer fizaranaId,
        @Param("ambaratonga") String ambaratonga,
        @Param("hasAssurance") Boolean hasAssurance
    );
}

