package tily.mg.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "fafi") // FAFI en malgache - remplace assurance
public class Fafi {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idfafi")
    private Integer id;

    @Column(name = "datepaiement")
    private LocalDate datePaiement;

    @Column(name = "montant", precision = 10, scale = 2)
    private BigDecimal montant;

    @Column(name = "statut", length = 20)
    private String statut;

    @OneToOne(mappedBy = "fafi")
    private Personne personne;

    // Constructors
    public Fafi() {}

    public Fafi(LocalDate datePaiement, BigDecimal montant, String statut) {
        this.datePaiement = datePaiement;
        this.montant = montant;
        this.statut = statut;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public LocalDate getDatePaiement() {
        return datePaiement;
    }

    public void setDatePaiement(LocalDate datePaiement) {
        this.datePaiement = datePaiement;
    }

    public BigDecimal getMontant() {
        return montant;
    }

    public void setMontant(BigDecimal montant) {
        this.montant = montant;
    }

    public String getStatut() {
        return statut;
    }

    public void setStatut(String statut) {
        this.statut = statut;
    }

    public Personne getPersonne() {
        return personne;
    }

    public void setPersonne(Personne personne) {
        this.personne = personne;
    }
}
