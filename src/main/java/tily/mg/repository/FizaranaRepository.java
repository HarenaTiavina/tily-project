package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tily.mg.entity.Fizarana;

import java.util.Optional;

@Repository
public interface FizaranaRepository extends JpaRepository<Fizarana, Integer> {
    Optional<Fizarana> findByNom(String nom);
}

