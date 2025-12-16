package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tily.mg.entity.DingamPiofanana;

import java.util.Optional;

@Repository
public interface DingamPiofananaRepository extends JpaRepository<DingamPiofanana, Integer> {
    Optional<DingamPiofanana> findByNom(String nom);
}

