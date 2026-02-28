package tily.mg.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entité pour les types/rôles de Filoha
 */
@Entity
@Table(name = "type_filoha")
public class TypeFiloha {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtypefiloha")
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100, unique = true)
    private String nom;

    @OneToMany(mappedBy = "typeFiloha")
    private List<Personne> personnes;

    // Constructors
    public TypeFiloha() {}

    public TypeFiloha(String nom) {
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

    @Override
    public String toString() {
        return "TypeFiloha{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
