package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tily.mg.entity.Utilisateur;

import java.util.List;
import java.util.Optional;

@Repository
public interface UtilisateurRepository extends JpaRepository<Utilisateur, Integer> {
    
    Optional<Utilisateur> findByEmail(String email);
    
    boolean existsByEmail(String email);
    
    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.fivondronana WHERE u.email = :email")
    Optional<Utilisateur> findByEmailWithFivondronana(@Param("email") String email);
    
    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.fivondronana WHERE u.id = :id")
    Optional<Utilisateur> findByIdWithDetails(@Param("id") Integer id);
    
    // Trouver tous les utilisateurs par Fivondronana
    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.fivondronana WHERE u.fivondronana.id = :fivondronanaId")
    List<Utilisateur> findByFivondronanaId(@Param("fivondronanaId") Integer fivondronanaId);
    
    // Trouver tous les utilisateurs non-admin
    @Query("SELECT u FROM Utilisateur u LEFT JOIN FETCH u.fivondronana WHERE u.role != 'ADMIN'")
    List<Utilisateur> findAllNonAdmin();
}
