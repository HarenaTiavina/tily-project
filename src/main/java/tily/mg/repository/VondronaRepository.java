package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tily.mg.entity.Vondrona;

import java.util.Optional;

@Repository
public interface VondronaRepository extends JpaRepository<Vondrona, Integer> {
    
    Optional<Vondrona> findByNom(String nom);
}

