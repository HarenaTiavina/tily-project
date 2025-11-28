package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tily.mg.entity.Andraikitra;

import java.util.Optional;

@Repository
public interface AndraikitraRepository extends JpaRepository<Andraikitra, Integer> {
    Optional<Andraikitra> findByNom(String nom);
}

