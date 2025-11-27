package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tily.mg.entity.TypePersonne;

import java.util.Optional;

@Repository
public interface TypePersonneRepository extends JpaRepository<TypePersonne, Integer> {
    Optional<TypePersonne> findByNom(String nom);
}

