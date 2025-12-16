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
    
    // ========== ADMIN: Toutes les personnes ==========
    
    // Find ALL responsables (admin)
    @Query("SELECT DISTINCT p FROM Personne p " +
           "LEFT JOIN FETCH p.fafi " +
           "LEFT JOIN FETCH p.secteur " +
           "LEFT JOIN FETCH p.andraikitra " +
           "LEFT JOIN FETCH p.fizarana " +
           "LEFT JOIN FETCH p.typePersonne " +
           "LEFT JOIN FETCH p.fivondronana " +
           "WHERE p.typePersonne.nom = 'Responsable'")
    List<Personne> findAllResponsables();
    
    // Find ALL eleves (admin)
    @Query("SELECT DISTINCT p FROM Personne p " +
           "LEFT JOIN FETCH p.fafi " +
           "LEFT JOIN FETCH p.secteur " +
           "LEFT JOIN FETCH p.fizarana " +
           "LEFT JOIN FETCH p.typePersonne " +
           "LEFT JOIN FETCH p.fivondronana " +
           "WHERE p.typePersonne.nom = 'Eleve'")
    List<Personne> findAllEleves();
    
    // ========== FIVONDRONANA: Personnes par Fivondronana ==========
    
    // Find responsables par Fivondronana
    @Query("SELECT DISTINCT p FROM Personne p " +
           "LEFT JOIN FETCH p.fafi " +
           "LEFT JOIN FETCH p.secteur " +
           "LEFT JOIN FETCH p.andraikitra " +
           "LEFT JOIN FETCH p.fizarana " +
           "LEFT JOIN FETCH p.typePersonne " +
           "LEFT JOIN FETCH p.fivondronana " +
           "WHERE p.typePersonne.nom = 'Responsable' " +
           "AND p.fivondronana.id = :fivondronanaId")
    List<Personne> findResponsablesByFivondronana(@Param("fivondronanaId") Integer fivondronanaId);
    
    // Find eleves par Fivondronana
    @Query("SELECT DISTINCT p FROM Personne p " +
           "LEFT JOIN FETCH p.fafi " +
           "LEFT JOIN FETCH p.secteur " +
           "LEFT JOIN FETCH p.fizarana " +
           "LEFT JOIN FETCH p.typePersonne " +
           "LEFT JOIN FETCH p.fivondronana " +
           "WHERE p.typePersonne.nom = 'Eleve' " +
           "AND p.fivondronana.id = :fivondronanaId")
    List<Personne> findElevesByFivondronana(@Param("fivondronanaId") Integer fivondronanaId);
    
    // Find by secteur
    List<Personne> findBySecteurId(Integer secteurId);
    
    // Find by fizarana
    List<Personne> findByFizaranaId(Integer fizaranaId);
    
    // Find by ambaratonga
    List<Personne> findByAmbaratonga(String ambaratonga);
    
    // Count by type
    @Query("SELECT COUNT(p) FROM Personne p WHERE p.typePersonne.nom = :typeName")
    Long countByTypePersonneNom(@Param("typeName") String typeName);
    
    // Count by type and fivondronana
    @Query("SELECT COUNT(p) FROM Personne p WHERE p.typePersonne.nom = :typeName AND p.fivondronana.id = :fivondronanaId")
    Long countByTypePersonneNomAndFivondronana(@Param("typeName") String typeName, @Param("fivondronanaId") Integer fivondronanaId);
    
    // Count responsables with fafi
    @Query("SELECT COUNT(p) FROM Personne p WHERE p.typePersonne.nom = 'Responsable' AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active'")
    Long countResponsablesWithFafi();
    
    // Count responsables with fafi by fivondronana
    @Query("SELECT COUNT(p) FROM Personne p WHERE p.typePersonne.nom = 'Responsable' AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active' AND p.fivondronana.id = :fivondronanaId")
    Long countResponsablesWithFafiByFivondronana(@Param("fivondronanaId") Integer fivondronanaId);
    
    // Count eleves with fafi
    @Query("SELECT COUNT(p) FROM Personne p WHERE p.typePersonne.nom = 'Eleve' AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active'")
    Long countElevesWithFafi();
    
    // Count eleves with fafi by fivondronana
    @Query("SELECT COUNT(p) FROM Personne p WHERE p.typePersonne.nom = 'Eleve' AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active' AND p.fivondronana.id = :fivondronanaId")
    Long countElevesWithFafiByFivondronana(@Param("fivondronanaId") Integer fivondronanaId);
    
    // Search
    @Query("SELECT p FROM Personne p WHERE " +
           "LOWER(p.nom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.prenom) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
           "LOWER(p.totem) LIKE LOWER(CONCAT('%', :search, '%'))")
    List<Personne> search(@Param("search") String search);
    
    // ========== ADMIN: Filter avec option Fivondronana ==========
    
    // Filter responsables (admin - avec filtre fivondronana optionnel)
    @Query("SELECT DISTINCT p FROM Personne p " +
           "LEFT JOIN FETCH p.fafi " +
           "LEFT JOIN FETCH p.secteur " +
           "LEFT JOIN FETCH p.andraikitra " +
           "LEFT JOIN FETCH p.fizarana " +
           "LEFT JOIN FETCH p.typePersonne " +
           "LEFT JOIN FETCH p.fivondronana " +
           "WHERE p.typePersonne.nom = 'Responsable' " +
           "AND (:fivondronanaId IS NULL OR p.fivondronana.id = :fivondronanaId) " +
           "AND (:secteurId IS NULL OR p.secteur.id = :secteurId) " +
           "AND (:andraikitraId IS NULL OR p.andraikitra.id = :andraikitraId) " +
           "AND (:fizaranaId IS NULL OR p.fizarana.id = :fizaranaId) " +
           "AND (:hasFafi IS NULL OR " +
           "(:hasFafi = true AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active') OR " +
           "(:hasFafi = false AND (p.fafi IS NULL OR p.fafi.statut != 'Active')))")
    List<Personne> filterResponsables(
        @Param("fivondronanaId") Integer fivondronanaId,
        @Param("secteurId") Integer secteurId,
        @Param("andraikitraId") Integer andraikitraId,
        @Param("fizaranaId") Integer fizaranaId,
        @Param("hasFafi") Boolean hasFafi
    );
    
    // Filter eleves (admin - avec filtre fivondronana optionnel)
    @Query("SELECT DISTINCT p FROM Personne p " +
           "LEFT JOIN FETCH p.fafi " +
           "LEFT JOIN FETCH p.secteur " +
           "LEFT JOIN FETCH p.fizarana " +
           "LEFT JOIN FETCH p.typePersonne " +
           "LEFT JOIN FETCH p.fivondronana " +
           "WHERE p.typePersonne.nom = 'Eleve' " +
           "AND (:fivondronanaId IS NULL OR p.fivondronana.id = :fivondronanaId) " +
           "AND (:secteurId IS NULL OR p.secteur.id = :secteurId) " +
           "AND (:fizaranaId IS NULL OR p.fizarana.id = :fizaranaId) " +
           "AND (:ambaratonga IS NULL OR p.ambaratonga = :ambaratonga) " +
           "AND (:hasFafi IS NULL OR " +
           "(:hasFafi = true AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active') OR " +
           "(:hasFafi = false AND (p.fafi IS NULL OR p.fafi.statut != 'Active')))")
    List<Personne> filterEleves(
        @Param("fivondronanaId") Integer fivondronanaId,
        @Param("secteurId") Integer secteurId,
        @Param("fizaranaId") Integer fizaranaId,
        @Param("ambaratonga") String ambaratonga,
        @Param("hasFafi") Boolean hasFafi
    );
    
    // ========== FIVONDRONANA: Filter par Fivondronana (non-admin) ==========
    
    // Filter responsables par Fivondronana
    @Query("SELECT DISTINCT p FROM Personne p " +
           "LEFT JOIN FETCH p.fafi " +
           "LEFT JOIN FETCH p.secteur " +
           "LEFT JOIN FETCH p.andraikitra " +
           "LEFT JOIN FETCH p.fizarana " +
           "LEFT JOIN FETCH p.typePersonne " +
           "LEFT JOIN FETCH p.fivondronana " +
           "WHERE p.typePersonne.nom = 'Responsable' " +
           "AND p.fivondronana.id = :fivondronanaId " +
           "AND (:secteurId IS NULL OR p.secteur.id = :secteurId) " +
           "AND (:andraikitraId IS NULL OR p.andraikitra.id = :andraikitraId) " +
           "AND (:fizaranaId IS NULL OR p.fizarana.id = :fizaranaId) " +
           "AND (:hasFafi IS NULL OR " +
           "(:hasFafi = true AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active') OR " +
           "(:hasFafi = false AND (p.fafi IS NULL OR p.fafi.statut != 'Active')))")
    List<Personne> filterResponsablesByFivondronana(
        @Param("fivondronanaId") Integer fivondronanaId,
        @Param("secteurId") Integer secteurId,
        @Param("andraikitraId") Integer andraikitraId,
        @Param("fizaranaId") Integer fizaranaId,
        @Param("hasFafi") Boolean hasFafi
    );
    
    // Filter eleves par Fivondronana
    @Query("SELECT DISTINCT p FROM Personne p " +
           "LEFT JOIN FETCH p.fafi " +
           "LEFT JOIN FETCH p.secteur " +
           "LEFT JOIN FETCH p.fizarana " +
           "LEFT JOIN FETCH p.typePersonne " +
           "LEFT JOIN FETCH p.fivondronana " +
           "WHERE p.typePersonne.nom = 'Eleve' " +
           "AND p.fivondronana.id = :fivondronanaId " +
           "AND (:secteurId IS NULL OR p.secteur.id = :secteurId) " +
           "AND (:fizaranaId IS NULL OR p.fizarana.id = :fizaranaId) " +
           "AND (:ambaratonga IS NULL OR p.ambaratonga = :ambaratonga) " +
           "AND (:hasFafi IS NULL OR " +
           "(:hasFafi = true AND p.fafi IS NOT NULL AND p.fafi.statut = 'Active') OR " +
           "(:hasFafi = false AND (p.fafi IS NULL OR p.fafi.statut != 'Active')))")
    List<Personne> filterElevesByFivondronana(
        @Param("fivondronanaId") Integer fivondronanaId,
        @Param("secteurId") Integer secteurId,
        @Param("fizaranaId") Integer fizaranaId,
        @Param("ambaratonga") String ambaratonga,
        @Param("hasFafi") Boolean hasFafi
    );
}
