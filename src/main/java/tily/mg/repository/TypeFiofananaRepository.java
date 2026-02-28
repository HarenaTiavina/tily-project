package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tily.mg.entity.TypeFiofanana;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeFiofananaRepository extends JpaRepository<TypeFiofanana, Integer> {
    
    Optional<TypeFiofanana> findByNom(String nom);
    
    List<TypeFiofanana> findAllByOrderByNomAsc();
}
