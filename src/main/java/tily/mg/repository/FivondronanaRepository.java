package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tily.mg.entity.Fivondronana;

import java.util.Optional;

@Repository
public interface FivondronanaRepository extends JpaRepository<Fivondronana, Integer> {
    
    Optional<Fivondronana> findByNom(String nom);
    
    Optional<Fivondronana> findByCode(String code);
}

