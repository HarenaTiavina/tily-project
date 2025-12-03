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
    private String ambaratonga; // Ambaratonga = Niveau en malgache

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
    @JoinColumn(name = "idfafi")
    private Fafi fafi; // FAFI en malgache - remplace assurance

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idsecteur")
    private Secteur secteur;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idsection")
    private Fizarana fizarana; // Fizarana = Section en malgache - utilisé pour les Beazina

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idandraikitra")
    private Andraikitra andraikitra; // Andraikitra en malgache - postes pour les Mpiandraikitra

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfivondronana")
    private Fivondronana fivondronana; // Fivondronana = District en malgache - chaque personne appartient à un Fivondronana

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

    public String getAmbaratonga() {
        return ambaratonga;
    }

    public void setAmbaratonga(String ambaratonga) {
        this.ambaratonga = ambaratonga;
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

    public Fafi getFafi() {
        return fafi;
    }

    public void setFafi(Fafi fafi) {
        this.fafi = fafi;
    }

    public Secteur getSecteur() {
        return secteur;
    }

    public void setSecteur(Secteur secteur) {
        this.secteur = secteur;
    }

    public Fizarana getFizarana() {
        return fizarana;
    }

    public void setFizarana(Fizarana fizarana) {
        this.fizarana = fizarana;
    }

    public Andraikitra getAndraikitra() {
        return andraikitra;
    }

    public void setAndraikitra(Andraikitra andraikitra) {
        this.andraikitra = andraikitra;
    }

    public Fivondronana getFivondronana() {
        return fivondronana;
    }

    public void setFivondronana(Fivondronana fivondronana) {
        this.fivondronana = fivondronana;
    }

    // Helper methods
    public String getNomComplet() {
        return prenom + " " + nom;
    }

    public boolean isMpiandraikitra() {
        return typePersonne != null && "Responsable".equalsIgnoreCase(typePersonne.getNom()); // Mpiandraikitra = Responsable en malgache
    }

    public boolean isBeazina() {
        return typePersonne != null && "Eleve".equalsIgnoreCase(typePersonne.getNom()); // Beazina = Élève en malgache
    }
    
    // Méthodes de compatibilité (deprecated)
    @Deprecated
    public boolean isResponsable() {
        return isMpiandraikitra();
    }

    @Deprecated
    public boolean isEleve() {
        return isBeazina();
    }

    public boolean hasFafi() {
        return fafi != null && "Active".equalsIgnoreCase(fafi.getStatut());
    }
}

