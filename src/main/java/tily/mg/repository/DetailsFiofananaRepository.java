package tily.mg.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tily.mg.entity.DetailsFiofanana;

import java.util.Optional;

@Repository
public interface DetailsFiofananaRepository extends JpaRepository<DetailsFiofanana, Integer> {
    
    @Query("SELECT d FROM DetailsFiofanana d " +
           "LEFT JOIN FETCH d.bitsikyFivondronana1 " +
           "LEFT JOIN FETCH d.bitsikyFivondronana2 " +
           "LEFT JOIN FETCH d.typeFiofanana " +
           "WHERE d.personne.id = :personneId AND d.typeFiofanana.id = :typeFiofananaId")
    Optional<DetailsFiofanana> findByPersonneIdAndTypeFiofananaId(
            @Param("personneId") Integer personneId,
            @Param("typeFiofananaId") Integer typeFiofananaId
    );
    
    @Query("SELECT d FROM DetailsFiofanana d " +
           "LEFT JOIN FETCH d.bitsikyFivondronana1 " +
           "LEFT JOIN FETCH d.bitsikyFivondronana2 " +
           "LEFT JOIN FETCH d.typeFiofanana " +
           "WHERE d.personne.id = :personneId")
    java.util.List<DetailsFiofanana> findAllByPersonneId(@Param("personneId") Integer personneId);
}
