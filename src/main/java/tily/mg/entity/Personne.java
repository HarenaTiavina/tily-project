package tily.mg.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "personne")
public class Personne {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idpersonne")
    private Integer id;

    @Column(name = "nom", nullable = false, length = 100)
    private String nom;

    @Column(name = "prenom", nullable = false, length = 100)
    private String prenom;

    @Column(name = "totem", length = 50)
    private String totem;

    @Column(name = "datenaissance")
    private LocalDate dateNaissance;

    @Column(name = "niveau", length = 50)
    private String niveau;

    @Column(name = "numerotelephone", length = 20)
    private String numeroTelephone;

    @Column(name = "image", columnDefinition = "TEXT")
    private String image;

    @Column(name = "numerocin", length = 30)
    private String numeroCin;

    @Column(name = "nompere", length = 100)
    private String nomPere;

    @Column(name = "nommere", length = 100)
    private String nomMere;

    @Column(name = "datefanekena")
    private LocalDate dateFanekena;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idtypepersonne")
    private TypePersonne typePersonne;

    @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "idassurance")
    private Assurance assurance;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idsecteur")
    private Secteur secteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idsection")
    private Section section;

    // Constructors
    public Personne() {}

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

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getTotem() {
        return totem;
    }

    public void setTotem(String totem) {
        this.totem = totem;
    }

    public LocalDate getDateNaissance() {
        return dateNaissance;
    }

    public void setDateNaissance(LocalDate dateNaissance) {
        this.dateNaissance = dateNaissance;
    }

    public String getNiveau() {
        return niveau;
    }

    public void setNiveau(String niveau) {
        this.niveau = niveau;
    }

    public String getNumeroTelephone() {
        return numeroTelephone;
    }

    public void setNumeroTelephone(String numeroTelephone) {
        this.numeroTelephone = numeroTelephone;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getNumeroCin() {
        return numeroCin;
    }

    public void setNumeroCin(String numeroCin) {
        this.numeroCin = numeroCin;
    }

    public String getNomPere() {
        return nomPere;
    }

    public void setNomPere(String nomPere) {
        this.nomPere = nomPere;
    }

    public String getNomMere() {
        return nomMere;
    }

    public void setNomMere(String nomMere) {
        this.nomMere = nomMere;
    }

    public LocalDate getDateFanekena() {
        return dateFanekena;
    }

    public void setDateFanekena(LocalDate dateFanekena) {
        this.dateFanekena = dateFanekena;
    }

    public TypePersonne getTypePersonne() {
        return typePersonne;
    }

    public void setTypePersonne(TypePersonne typePersonne) {
        this.typePersonne = typePersonne;
    }

    public Assurance getAssurance() {
        return assurance;
    }

    public void setAssurance(Assurance assurance) {
        this.assurance = assurance;
    }

    public Secteur getSecteur() {
        return secteur;
    }

    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
    }

    public Section getSection() {
        return section;
    }

    public void setSection(Section section) {
        this.section = section;
    }

    // Helper methods
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean isResponsable() {
        return typePersonne != null && "Responsable".equalsIgnoreCase(typePersonne.getNom());
    }

    public boolean isEleve() {
        return typePersonne != null && "Eleve".equalsIgnoreCase(typePersonne.getNom());
    }

    public boolean hasAssurance() {
        return assurance != null && "Active".equalsIgnoreCase(assurance.getStatut());
    }
}

