package tily.mg.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entité DingamPiofanana (Niveau de formation des Mpiandraikitra)
 * Options: Fanomanana, Fanaterana, Miandry Ravinala, Ravinala, TP2
 */
@Entity
@Table(name = "dingam_piofanana")
public class DingamPiofanana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "iddingampiofanana")
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @OneToMany(mappedBy = "dingamPiofanana")
    private List<Personne> personnes;

    // Constructors
    public DingamPiofanana() {}

    public DingamPiofanana(String nom) {
        this.nom = nom;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public List<Personne> getPersonnes() {
        return personnes;
    }

    public void setPersonnes(List<Personne> personnes) {
        this.personnes = personnes;
    }
}

