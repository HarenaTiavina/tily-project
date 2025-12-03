package tily.mg.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entité Vondrona (Groupe en français)
 * Les Beazina et Mpiandraikitra appartiennent à un Vondrona
 */
@Entity
@Table(name = "vondrona")
public class Vondrona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idvondrona")
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @OneToMany(mappedBy = "vondrona")
    private List<Personne> personnes;

    // Constructors
    public Vondrona() {}

    public Vondrona(String nom) {
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

