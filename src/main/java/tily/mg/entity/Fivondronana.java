package tily.mg.entity;

import jakarta.persistence.*;
import java.util.List;

/**
 * Entité Fivondronana (District/Zone en français)
 * Les utilisateurs sont liés à un Fivondronana et ne peuvent voir/créer que les personnes de leur Fivondronana
 * L'admin peut voir tous les Fivondronana
 */
@Entity
@Table(name = "fivondronana")
public class Fivondronana {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfivondronana")
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "code", length = 20)
    private String code;

    @OneToMany(mappedBy = "fivondronana")
    private List<Personne> personnes;

    @OneToMany(mappedBy = "fivondronana")
    private List<Utilisateur> utilisateurs;

    // Constructors
    public Fivondronana() {}

    public Fivondronana(String nom) {
        this.nom = nom;
    }

    public Fivondronana(String nom, String code) {
        this.nom = nom;
        this.code = code;
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

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Personne> getPersonnes() {
        return personnes;
    }

    public void setPersonnes(List<Personne> personnes) {
        this.personnes = personnes;
    }

    public List<Utilisateur> getUtilisateurs() {
        return utilisateurs;
    }

    public void setUtilisateurs(List<Utilisateur> utilisateurs) {
        this.utilisateurs = utilisateurs;
    }
}

