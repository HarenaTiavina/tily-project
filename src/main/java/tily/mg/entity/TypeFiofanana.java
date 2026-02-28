package tily.mg.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "type_fiofanana")
public class TypeFiofanana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idtypefiofanana")
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100, unique = true)
    private String nom;

    // Constructors
    public TypeFiofanana() {}

    public TypeFiofanana(String nom) {
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

    @Override
    public String toString() {
        return "TypeFiofanana{" +
                "id=" + id +
                ", nom='" + nom + '\'' +
                '}';
    }
}
