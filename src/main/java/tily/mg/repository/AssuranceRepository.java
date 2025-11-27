package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tily.mg.entity.Assurance;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface AssuranceRepository extends JpaRepository<Assurance, Integer> {
    List<Assurance> findByStatut(String statut);
    
    @Query("SELECT SUM(a.montant) FROM Assurance a WHERE a.statut = 'Active'")
    BigDecimal getTotalMontantActive();
    
    @Query("SELECT COUNT(a) FROM Assurance a WHERE a.statut = 'Active'")
    Long countActive();
}

