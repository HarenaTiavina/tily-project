package tily.mg.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "utilisateur")
public class Utilisateur {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idutilisateur")
    private Integer id;

    @Column(name = "email", nullable = false, unique = true, length = 150)
    private String email;

    @Column(name = "motdepasse", nullable = false)
    private String motDePasse;

    @Column(name = "role", length = 20)
    private String role; // ADMIN, DFAF, USER

    @Column(name = "actif")
    private Boolean actif = true;

    @Column(name = "datecreation")
    private LocalDateTime dateCreation;

    @Column(name = "derniereconnexion")
    private LocalDateTime derniereConnexion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "idfivondronana")
    private Fivondronana fivondronana;

    // Constructors
    public Utilisateur() {
        this.dateCreation = LocalDateTime.now();
        this.actif = true;
        this.role = "USER";
    }

    public Utilisateur(String email, String motDePasse) {
        this();
        this.email = email;
        this.motDePasse = motDePasse;
    }

    // Getters and Setters
    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMotDePasse() {
        return motDePasse;
    }

    public void setMotDePasse(String motDePasse) {
        this.motDePasse = motDePasse;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public Boolean getActif() {
        return actif;
    }

    public void setActif(Boolean actif) {
        this.actif = actif;
    }

    public LocalDateTime getDateCreation() {
        return dateCreation;
    }

    public void setDateCreation(LocalDateTime dateCreation) {
        this.dateCreation = dateCreation;
    }

    public LocalDateTime getDerniereConnexion() {
        return derniereConnexion;
    }

    public void setDerniereConnexion(LocalDateTime derniereConnexion) {
        this.derniereConnexion = derniereConnexion;
    }

    public Fivondronana getFivondronana() {
        return fivondronana;
    }

    public void setFivondronana(Fivondronana fivondronana) {
        this.fivondronana = fivondronana;
    }

    // Helper methods
    public String getNomComplet() {
        if (fivondronana != null) {
            return fivondronana.getNom();
        }
        return email;
    }

    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isDfaf() {
        return "DFAF".equals(role);
    }

    /**
     * Vérifie si l'utilisateur a des droits administratifs (ADMIN ou DFAF)
     * Les deux peuvent voir toutes les données mais seul ADMIN peut créer des comptes
     */
    public boolean hasAdminAccess() {
        return "ADMIN".equals(role) || "DFAF".equals(role);
    }

    public Integer getFivondronanaId() {
        return fivondronana != null ? fivondronana.getId() : null;
    }
}

