package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tily.mg.entity.TypeFiloha;

import java.util.List;
import java.util.Optional;

@Repository
public interface TypeFilohaRepository extends JpaRepository<TypeFiloha, Integer> {
    
    Optional<TypeFiloha> findByNom(String nom);
    
    List<TypeFiloha> findAllByOrderByNomAsc();
}
