package tily.mg.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tily.mg.entity.Personne;
import tily.mg.entity.TypePersonne;
import tily.mg.entity.Utilisateur;
import tily.mg.repository.TypePersonneRepository;
import tily.mg.repository.UtilisateurRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
@Transactional
public class AuthService {

    @Autowired
    private UtilisateurRepository utilisateurRepository;

    @Autowired
    private TypePersonneRepository typePersonneRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * Inscription d'un nouvel utilisateur
     */
    public Utilisateur inscrire(String email, String motDePasse, String nom, String prenom,
                                 String typePersonne, LocalDate dateNaissance) {
        
        // Vérifier si l'email existe déjà
        if (utilisateurRepository.existsByEmail(email)) {
            throw new RuntimeException("Cet email est déjà utilisé");
        }

        // Créer la personne
        Personne personne = new Personne();
        personne.setNom(nom);
        personne.setPrenom(prenom);
        personne.setDateNaissance(dateNaissance);

        // Définir le type de personne
        Optional<TypePersonne> type = typePersonneRepository.findByNom(typePersonne);
        type.ifPresent(personne::setTypePersonne);

        // Créer l'utilisateur
        Utilisateur utilisateur = new Utilisateur();
        utilisateur.setEmail(email);
        utilisateur.setMotDePasse(passwordEncoder.encode(motDePasse));
        utilisateur.setPersonne(personne);
        utilisateur.setRole("USER");
        utilisateur.setActif(true);
        utilisateur.setDateCreation(LocalDateTime.now());

        return utilisateurRepository.save(utilisateur);
    }

    /**
     * Authentification d'un utilisateur
     */
    public Optional<Utilisateur> authentifier(String email, String motDePasse) {
        Optional<Utilisateur> utilisateurOpt = utilisateurRepository.findByEmailWithPersonne(email);
        
        if (utilisateurOpt.isPresent()) {
            Utilisateur utilisateur = utilisateurOpt.get();
            
            // Vérifier si le compte est actif
            if (!utilisateur.getActif()) {
                return Optional.empty();
            }
            
            // Vérifier le mot de passe
            if (passwordEncoder.matches(motDePasse, utilisateur.getMotDePasse())) {
                // Mettre à jour la dernière connexion
                utilisateur.setDerniereConnexion(LocalDateTime.now());
                utilisateurRepository.save(utilisateur);
                return Optional.of(utilisateur);
            }
        }
        
        return Optional.empty();
    }

    /**
     * Trouver un utilisateur par email
     */
    public Optional<Utilisateur> findByEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return Optional.empty();
        }
        // Utiliser directement la méthode avec JOIN FETCH pour charger la personne et le fafi
        return utilisateurRepository.findByEmailWithPersonne(email.trim());
    }

    /**
     * Trouver un utilisateur par ID avec détails
     */
    public Optional<Utilisateur> findById(Integer id) {
        return utilisateurRepository.findByIdWithDetails(id);
    }

    /**
     * Vérifier si un email existe
     */
    public boolean emailExists(String email) {
        return utilisateurRepository.existsByEmail(email);
    }

    /**
     * Mettre à jour le mot de passe
     */
    public void updatePassword(Integer userId, String newPassword) {
        utilisateurRepository.findById(userId).ifPresent(user -> {
            user.setMotDePasse(passwordEncoder.encode(newPassword));
            utilisateurRepository.save(user);
        });
    }
}

