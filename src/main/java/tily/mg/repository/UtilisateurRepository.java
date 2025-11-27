package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tily.mg.entity.Utilisateur;

import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    
    Optional<Utilisateur> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.personne WHERE u.email = :email")
    Optional<Utilisateur> findByEmailWithPersonne(@Param("email") String email);
    
    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.personne p LEFT JOIN FETCH p.typePersonne WHERE u.id = :id")
    Optional<Utilisateur> findByIdWithDetails(@Param("id") Integer id);
}

