package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tily.mg.entity.Fafi;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface FafiRepository extends JpaRepository<Fafi, Integer> {
    List<Fafi> findByStatut(String statut);
    
    @Query("SELECT SUM(f.montant) FROM Fafi f WHERE f.statut = 'Active'")
    BigDecimal getTotalMontantActive();
    
    @Query("SELECT COUNT(f) FROM Fafi f WHERE f.statut = 'Active'")
    Long countActive();
    
    @Query("SELECT DISTINCT f.statut FROM Fafi f WHERE f.statut IS NOT NULL ORDER BY f.statut")
    List<String> findDistinctStatuts();
    
    // Méthodes filtrées par Fivondronana
    @Query("SELECT SUM(f.montant) FROM Fafi f WHERE f.statut = 'Active' AND f.personne.fivondronana.id = :fivondronanaId")
    BigDecimal getTotalMontantActiveByFivondronana(Integer fivondronanaId);
    
    @Query("SELECT COUNT(f) FROM Fafi f WHERE f.statut = 'Active' AND f.personne.fivondronana.id = :fivondronanaId")
    Long countActiveByFivondronana(Integer fivondronanaId);
}

